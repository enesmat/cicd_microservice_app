pipeline {
  agent any

  parameters {
    string(name: 'BUILD_ENV', defaultValue: 'dev', description: 'dev oder prod')
  }

  environment {
    IMAGE_BACKEND = "kukuk-backend"
    IMAGE_FRONTEND = "kukuk-frontend"
    
    REGISTRY_URL = "registrykurs1.azurecr.io"
  }

  stages {

    stage('Build Backend') {
      steps {
        dir('backend') {
          sh "mvn clean package -P${params.BUILD_ENV}"
        }
      }
    }

    stage('Test Backend') {
      steps {
        dir('backend') {
          sh "mvn test"
        }
      }
    }

    stage('Build Frontend') {
      steps {
        dir('frontend') {
          sh "npm install"
          sh "npm run build"
        }
      }
    }


    stage('Test Frontend') {
      steps {
        dir('frontend') {
          sh "npm test || true"
        }
      }
    }


    stage('Docker Build') {
      steps {
        sh "docker build -t ${REGISTRY_URL}/${IMAGE_BACKEND}:${params.BUILD_ENV} ./backend"
        sh "docker build -t ${REGISTRY_URL}/${IMAGE_FRONTEND}:${params.BUILD_ENV} ./frontend"
      }
    }


    stage('Docker Push') {
      steps {
        withCredentials([usernamePassword(credentialsId: 'azure-credentials', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
          sh "echo $DOCKER_PASS | docker login $REGISTRY_URL -u $DOCKER_USER --password-stdin"
          sh "docker push ${REGISTRY_URL}/${IMAGE_BACKEND}:${params.BUILD_ENV}"
          sh "docker push ${REGISTRY_URL}/${IMAGE_FRONTEND}:${params.BUILD_ENV}"
        }
      }
    }


    stage('Deploy Dev') {
      when {
        expression { params.BUILD_ENV == 'dev' }
      }
      steps {
        catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
          withCredentials([file(credentialsId: 'enes-dev', variable: 'KUBECONFIG')]) {
            sh "kubectl apply -f k8s/dev/backend-deployment.yaml"
            sh "kubectl apply -f k8s/dev/backend-service.yaml"
            sh "kubectl apply -f k8s/dev/frontend-deployment.yaml"
            sh "kubectl apply -f k8s/dev/frontend-service.yaml"
          }
        }
      }
    }


    stage('Deploy Prod') {
      when {
        expression { params.BUILD_ENV == 'prod' }
      }
      steps {
        catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
          withCredentials([file(credentialsId: 'enes-prod', variable: 'KUBECONFIG')]) {
            sh "kubectl apply -f k8s/prod/backend-deployment.yaml"
            sh "kubectl apply -f k8s/prod/backend-service.yaml"
            sh "kubectl apply -f k8s/prod/frontend-deployment.yaml"
            sh "kubectl apply -f k8s/prod/frontend-service.yaml"
          }
        }
      }
    }

  } 

  
  post {
    always {
      sh 'rm -rf ./*'
    }
    success {
      echo " Jenkins-Pipeline erfolgreich abgeschlossen"
    }
    failure {
      echo " Fehler in der Jenkins-Pipeline aufgetreten"
    }
  }
} 