plugins {
  id("common-conventions")
  id("net.kyori.blossom")
}

sourceSets {
  main {
    blossom {
      javaSources {
        property("version", project.versionString())
        property("git_branch", project.currentBranch())
        property("git_commit", project.lastCommitHash())
      }
    }
  }
}
