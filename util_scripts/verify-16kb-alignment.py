#!/usr/bin/env python3
"""Verify every bundled .so supports 16 KB memory pages.

Google Play requires all apps targeting Android 15+ to support 16 KB memory
page sizes. A shared library complies when every PT_LOAD segment is aligned
to at least 16384 bytes, which the linker does with:

    -Wl,-z,max-page-size=16384

Usage:
    util_scripts/verify-16kb-alignment.py <file.aar|apk|aab|zip|dir>

Exits non-zero if any bundled shared library fails.
"""
import glob
import os
import struct
import sys
import tempfile
import zipfile

REQUIRED_ALIGN = 16 * 1024
PT_LOAD = 1


def load_segment_aligns(path):
    """Return p_align for every PT_LOAD segment, or None if not an ELF file."""
    with open(path, "rb") as f:
        head = f.read(64)
        if len(head) < 64 or head[:4] != b"\x7fELF":
            return None
        is64 = head[4] == 2
        endian = "<" if head[5] == 1 else ">"
        if is64:
            ph_off = struct.unpack_from(endian + "Q", head, 0x20)[0]
            ph_entsize = struct.unpack_from(endian + "H", head, 0x36)[0]
            ph_num = struct.unpack_from(endian + "H", head, 0x38)[0]
        else:
            ph_off = struct.unpack_from(endian + "I", head, 0x1C)[0]
            ph_entsize = struct.unpack_from(endian + "H", head, 0x2A)[0]
            ph_num = struct.unpack_from(endian + "H", head, 0x2C)[0]
        f.seek(ph_off)
        table = f.read(ph_entsize * ph_num)

    aligns = []
    for i in range(ph_num):
        off = i * ph_entsize
        if off + ph_entsize > len(table):
            break
        if struct.unpack_from(endian + "I", table, off)[0] != PT_LOAD:
            continue
        if is64:
            aligns.append(struct.unpack_from(endian + "Q", table, off + 0x30)[0])
        else:
            aligns.append(struct.unpack_from(endian + "I", table, off + 0x1C)[0])
    return aligns


def check_dir(root):
    failures = []
    checked = 0
    for so in sorted(glob.glob(os.path.join(root, "**", "*.so"), recursive=True)):
        aligns = load_segment_aligns(so)
        if aligns is None:
            continue
        checked += 1
        # The weakest segment decides compliance: one under-aligned PT_LOAD
        # is enough to break loading on a 16 KB page kernel.
        worst = min(aligns) if aligns else 0
        name = os.path.relpath(so, root)
        if worst < REQUIRED_ALIGN:
            failures.append(name)
            print("FAIL  align=0x%x  %s" % (worst, name))
        else:
            print("PASS  align=0x%x  %s" % (worst, name))
    return failures, checked


def main():
    if len(sys.argv) != 2:
        print(__doc__)
        return 2
    target = sys.argv[1]

    if not os.path.exists(target):
        print("ERROR: no such file or directory: %s" % target)
        return 1

    if os.path.isdir(target):
        failures, checked = check_dir(target)
    else:
        with tempfile.TemporaryDirectory() as tmp:
            with zipfile.ZipFile(target) as z:
                for name in z.namelist():
                    if name.endswith(".so"):
                        z.extract(name, tmp)
            failures, checked = check_dir(tmp)

    if checked == 0:
        print("ERROR: no shared libraries found in %s; nothing was verified" % target)
        return 1

    print("\nChecked %d shared libraries, %d failed." % (checked, len(failures)))
    if failures:
        print("Not 16 KB page size compatible. Rebuild the native libraries with")
        print("-Wl,-z,max-page-size=16384 (NDK r27+).")
        return 1
    print("All shared libraries are 16 KB page size compatible.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
