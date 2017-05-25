#!/bin/bash
# Output command before executing
set -x

# Exit on error
set -e

# Source environment variables of the jenkins slave
# that might interest this worker.
function load_jenkins_vars() {
  if [ -e "jenkins-env" ]; then
    cat jenkins-env \
      | grep -E "(JENKINS_URL|GIT_BRANCH|GIT_COMMIT|BUILD_NUMBER|ghprbSourceBranch|ghprbActualCommit|BUILD_URL|ghprbPullId)=" \
      | sed 's/^/export /g' \
      > ~/.jenkins-env
    source ~/.jenkins-env
  fi
}

function install_deps() {
  # We need to disable selinux for now, XXX
  /usr/sbin/setenforce 0

  # Get all the deps in
  yum -y install \
    docker \
    make \
    git \
    wget \
    curl

  wget http://repos.fedorapeople.org/repos/dchen/apache-maven/epel-apache-maven.repo -O /etc/yum.repos.d/epel-apache-maven.repo
  yum -y install apache-maven

  service docker start
  echo 'CICO: Dependencies installed'
}

function run_tests() {
  echo 'CICO: Run mv clean install -pl :keycloak-server-dist -am -Pdistribution'
  mvn clean install -pl :keycloak-server-dist -am -Pdistribution

  echo 'CICO: keycloak-server tests completed successfully!'
}

load_jenkins_vars;
install_deps;

run_tests;
