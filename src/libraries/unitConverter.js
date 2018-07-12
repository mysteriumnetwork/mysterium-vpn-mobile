/*
 * Copyright (C) 2017 The "MysteriumNetwork/mysterion" Authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

const KB = 1024
const MB = 1024 * KB
const GB = 1024 * MB

function bytesDisplay (bytes) {
  if (bytes < KB) {
    return bytes + ' B'
  } else if (bytes < MB) {
    return (bytes / KB).toFixed(2) + ' KB'
  } else if (bytes < GB) {
    return (bytes / MB).toFixed(2) + ' MB'
  } else {
    return (bytes / GB).toFixed(2) + ' GB'
  }
}

/**
 * @function
 * @param {number} val
 * @returns {string} readable in --:--:-- format
 * @throws {Error} if argument is null
 */
function timeDisplay (val) {
  if (typeof val !== 'number' || val < 0) {
    throw new Error('invalid input')
  }
  let h = Math.floor(val / 3600)
  h = h > 9 ? h : '0' + h
  let m = Math.floor((val % 3600) / 60)
  m = m > 9 ? m : '0' + m
  let s = (val % 60)
  s = s > 9 ? s : '0' + s
  return `${h}:${m}:${s}`
}

export {
  bytesDisplay,
  timeDisplay
}
