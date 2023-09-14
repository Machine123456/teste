pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scmGit(branches: [[name: '*/main']], userRemoteConfigs: [[url: 'https://github.com/Machine123456/authentication-service.git']])
            }
        }
        
        stage("Build JAR") {
            steps {
                sh "cd ./app && mvn clean package"
            }
        }
        
        stage ("Build Image") {
            steps {
                script {
                    dockerImage = docker.build("andrealao/microservices:authentication-service-latest") //tag will be replaced in push step
                }
            }
        }
        
        stage ("Push to Docker Hub") {
                steps {
                    script{
                        withDockerRegistry([ credentialsId: "dockerHubAccount", url: "" ]) {
                            dockerImage.push("authentication-service-1.$BUILD_NUMBER")
                        }
                    }
                }   
        }
    }
}
