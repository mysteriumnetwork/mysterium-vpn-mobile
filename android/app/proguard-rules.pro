# R8 rules for the release build.
#
# Only rules that nothing else supplies belong here. AGP's
# proguard-android-optimize.txt and each library's own consumer rules are merged
# in automatically; the fully merged result is written to
# build/outputs/mapping/<variant>/configuration.txt. Check there before adding a
# rule -- the reflection-driven keeps for Room, WorkManager, Retrofit, Gson,
# Parcelable and kotlin-reflect all arrive from the dependency that needs them,
# and restating one here only risks overriding it with a weaker form.

# Play Console deobfuscates crash traces from the mapping file bundled into the
# AAB. Not supplied by the default rules.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# --- gomobile / mobile-node -------------------------------------------------
# The Go runtime resolves these by name over JNI. The mobile-node AAR ships the
# same two rules, but that AAR is fetched from a GitHub release by the
# downloadMobileNode task rather than resolved through Maven, so they are
# restated here: this is the one path where a silently missing keep rule takes
# out the VPN itself.
-keep class go.** { *; }
-keep class mysterium.** { *; }

# Called back from Go through mysterium.WireguardTunnelSetup. Ordinary
# reachability would keep it -- it is constructed in MysteriumAndroidCoreService
# and its methods override an interface kept above -- but it costs under 1 KB
# and sits on the tunnel path, so it stays explicit.
-keep class updated.mysterium.vpn.core.WireguardAndroidTunnelSetup { *; }

# --- JSON models ------------------------------------------------------------
# NodeRepository builds Moshi with KotlinJsonAdapterFactory (reflective, not
# codegen), and PaymentUseCase/FilterUseCase/StatisticUseCase call Gson
# directly, so field names in this package must survive.
#
# This is deliberately whole-package, and it is the most expensive rule in the
# file: ~105 classes, ~64 KB of DEX, roughly 1.3 points of the obfuscation
# score. It cannot simply be narrowed -- OrderOptions.minimum and
# PaymentGateway.name/currencies are deserialized by Gson with no
# @SerializedName, so renaming them yields a silent null rather than a crash.
# Narrowing means first auditing every serialized field for an explicit
# @SerializedName/@Json; migrating Moshi to codegen (KSP) would retire the rule
# outright, along with the 3.5 MB kotlin-reflect dependency behind it.
-keep class updated.mysterium.vpn.model.** { *; }

# kotlin.jvm.internal.Reflection loads this by name and kotlin-reflect ships no
# rule for it. The rest of kotlin-reflect is reached through ordinary calls.
-keep class kotlin.reflect.jvm.internal.ReflectionFactoryImpl { *; }

# --- Warnings ---------------------------------------------------------------
# Referenced by libraries but never packaged. The javax.annotation and
# kotlin.reflect equivalents come from the libraries themselves.
-dontwarn org.jetbrains.annotations.**
