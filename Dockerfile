FROM registry.access.redhat.com/redhat-openjdk-18/openjdk18-openshift
MAINTAINER shawnyhw6n9 <sunkistfish@hotmail.com>

RUN \
yum -y install git

RUN yum clean all
