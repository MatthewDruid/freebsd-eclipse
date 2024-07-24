#
# Configuration
#
ECLIPSE_TAG="R4_32"

ECLIPSE_TOP="eclipse-platform/eclipse.platform.releng.aggregator"
ECLIPSE_MODULES="
	eclipse-jdt/eclipse.jdt
	eclipse-jdt/eclipse.jdt.core
	eclipse-jdt/eclipse.jdt.core.binaries
	eclipse-jdt/eclipse.jdt.debug
	eclipse-jdt/eclipse.jdt.ui
	eclipse-pde/eclipse.pde
	eclipse-platform/eclipse.platform
	eclipse-platform/eclipse.platform.swt
	eclipse-platform/eclipse.platform.ui
	eclipse-equinox/equinox.binaries:rt.equinox.binaries
	eclipse-equinox/equinox
	eclipse-equinox/p2:rt.equinox.p2"

PATCH_DIR="java-eclipse/files"
PATCHED_DIRS="
	eclipse-platform-parent
	eclipse.pde
	eclipse.platform
	eclipse.platform.releng
	eclipse.platform.releng.tychoeclipsebuilder
	eclipse.platform.swt
	eclipse.platform.swt.binaries
	eclipse.platform.text
	eclipse.platform.ua
	eclipse.platform.ui
	equinox
	rt.equinox.p2
"
