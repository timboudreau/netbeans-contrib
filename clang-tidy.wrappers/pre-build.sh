#!/bin/bash -x

export download_dir="$1"
export projects_dir="$2"
export netbeans="${projects_dir}/../../../netbeans"
export harness="${projects_dir}/../../../netbeans/harness"

assemble() {
  local platform=$1

  # erase contents of project's binary dir
  local project_bin="${projects_dir}/${platform}/release/clang-tidy"
  rm -rf   "$project_bin"
  mkdir -p "$project_bin"

  cp "${download_dir}/clang-tidy-${platform}" "$project_bin"

  ant -f ${projects_dir}/${platform} -Ddo.not.clean.module.config.xml=true -Dcontinue.after.failing.tests=true -Dnbplatform.NetBeans_IDE_CND_Dev.netbeans.dest.dir="$netbeans" -Dnbplatform.NetBeans_IDE_CND_Release.netbeans.dest.dir="$netbeans" -Dnbplatform.NetBeans_IDE_CND_Dev.harness.dir="$harness" -Dnbplatform.NetBeans_IDE_CND_Release.harness.dir="$harness" clean netbeans
  ant -f ${projects_dir}/${platform} -Dcontinue.after.failing.tests=true -Dnbplatform.NetBeans_IDE_CND_Dev.netbeans.dest.dir="$netbeans" -Dnbplatform.NetBeans_IDE_CND_Release.netbeans.dest.dir="$netbeans" -Dnbplatform.NetBeans_IDE_CND_Dev.harness.dir="$harness" -Dnbplatform.NetBeans_IDE_CND_Release.harness.dir="$harness" -Dkeystore=${WORKSPACE}/keystore/key.priv -Dnbm_alias=nb_ide -Dstorepass=ch0b0t7 nbm
}

assemble "Linux_x86_64"
