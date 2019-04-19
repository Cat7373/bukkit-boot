#!/usr/bin/env bash

# ----------------------------------------------------------------------------
# 启动调试服务器的脚本
# ----------------------------------------------------------------------------

# 跳到当前目录
cd `dirname "$0"`

# ----------------------------------------------------------------------------
# 将相对路径转换为绝对路径
# Arguments:
#   $1 path 被转换的路径
# Returns:
#   stdout 转换后的路径
# ----------------------------------------------------------------------------
function realpath() {
  echo $(cd "$1" && pwd)
}

# ----------------------------------------------------------------------------
# Global Variables:
#   BASE_DIR 项目根目录的绝对路径
#   BUKKIT_BOOT_DIR 框架根目录的绝对路径
#   SERVER_DIR 测试服务器所在目录的绝对路径
# ----------------------------------------------------------------------------
readonly BASE_DIR="$(realpath "$(pwd)/../..")"
readonly BUKKIT_BOOT_DIR="${BASE_DIR}/bukkit-boot"
readonly SERVER_DIR="$(pwd)/runtime"

# 删除之前的测试插件
rm -fv ${SERVER_DIR}/plugins/*.jar

# 重新构建测试插件
cd ${BASE_DIR}
${BASE_DIR}/gradlew clean jar

# 拷贝构建结果到插件目录
cp -v ${BUKKIT_BOOT_DIR}/build/libs/*.jar ${SERVER_DIR}/plugins/
cp -v ${BASE_DIR}/examples/demo-plugin/build/libs/*.jar ${SERVER_DIR}/plugins/
cp -v ${BASE_DIR}/examples/getcommand/build/libs/*.jar ${SERVER_DIR}/plugins/
