#!/usr/bin/env groovy

pipeline {
    agent {
        kubernetes {
            // label 'kube-slave-docker'
            defaultContainer 'jnlp'
            slaveConnectTimeout 200
            yamlFile 'cicd/pod-godocker.yml'
        }
    }

    options {
        ansiColor('xterm')
        // parallelsAlwaysFailFast()
        timestamps()
        skipDefaultCheckout()
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    environment {
        // REGISTRY = "dimdiden/kubectl-arm"
        DOCKER_HUB_CREDS = 'docker-hub-connector'
    }

    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/argoproj/argo-cd.git'
            }
        }
        stage('Build-Push') {
            steps {
                container('godocker') {
                    script {
                        sh "make armimage"
                    }
                }
            }
        }
    }
}