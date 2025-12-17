pipeline {
    agent any

    tools {
        jdk 'JAVA_HOME'
        maven 'MAVEN'
    }

    environment {
        DOCKERHUB_REPO = "kalyanpologi0027/user-service"
        IMAGE_TAG = "latest"
    }

    stages {

        stage('Checkout Code') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/kalyan-pologi/MicroServices.git'
            }
        }

        stage('Build service Registry') {
            steps {
                bat '''
                    cd serviceRegistry
                    mvn clean package -DskipTests
                '''
            }
        }

        stage('Build ApiGateway') {
            steps {
                bat '''
                    cd ApiGateway
                    mvn clean package -DskipTests
                '''
            }
        }

        stage('Build ConfigServer') {
            steps {
                bat '''
                    cd ConfigServer
                    mvn clean package -DskipTests
                '''
            }
        }

        stage('Build userService') {
            steps {
                bat '''
                    cd userService
                    mvn clean package
                '''
            }
        }
        stage('Build Docker Image - userService') {
            steps {
                bat '''
                    cd userService
                    docker build -t user-service:latest .
                '''
            }
        }

        stage('Docker Login') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-creds',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    bat '''
                        docker login -u %DOCKER_USER% -p %DOCKER_PASS%
                    '''
                }
            }
        }

        stage('Tag & Push to Docker Hub') {
            steps {
                bat '''
                    docker tag user-service:latest %DOCKERHUB_REPO%:%IMAGE_TAG%
                    docker push %DOCKERHUB_REPO%:%IMAGE_TAG%
                '''
            }
        }

        stage('Archive All JARs') {
            steps {
                archiveArtifacts artifacts: '''
                    serviceRegistry/target/*.jar,
                    ApiGateway/target/*.jar,
                    ConfigServer/target/*.jar,
                    userService/target/*.jar
                ''', fingerprint: true
            }
        }
    }

    post {
        success {
            echo '✅ CI successful: Docker image pushed to Docker Hub'
        }
        failure {
            echo '❌ CI failed'
        }
    }
}
