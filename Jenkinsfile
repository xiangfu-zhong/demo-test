pipeline {
    agent any

    stages {
        stage('git checkout') {
            steps {
                checkout scmGit(branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[credentialsId: 'c0989290-8949-44cb-b6c3-7ec9bc793815', url: 'https://gitlab.com/zxf2/demo-tet.git']])
            }
        }
        
        stage('maven build') {
            steps {
                sh 'mvn clean package -Dmaven.test.skip=true'
            }
        }
        
        stage('upload image') {
            steps {
                echo 'upload image..'
                sh '''
                    REPOSITORY=192.168.126.140:80/repo/mytest:${tag}				
                    cd docker
                    mv ../target/*.jar ./		
                    docker build -t $REPOSITORY .
                    docker login 192.168.126.140:80/ -u admin -p Harbor12345
                    docker push $REPOSITORY
                    docker logout  192.168.126.140
                '''
            }
        }
        
        stage('deploy') {
            steps {
                echo 'deploy..'
sshPublisher(publishers: [sshPublisherDesc(configName: 'k8s_server', transfers: [sshTransfer(cleanRemote: false, excludes: '', execCommand: '''REPOSITORY=192.168.126.140:80/repo/mytest:${tag}
docker stop mytest
docker rm -f mytest
docker rmi -f $REPOSITORY
docker login 192.168.126.140:80/ -u admin -p Harbor12345
docker pull $REPOSITORY
docker run -d --name mytest -p 8081:8080 $REPOSITORY''', execTimeout: 120000, flatten: false, makeEmptyDirs: false, noDefaultExcludes: false, patternSeparator: '[, ]+', remoteDirectory: '', remoteDirectorySDF: false, removePrefix: '', sourceFiles: '')], usePromotionTimestamp: false, useWorkspaceInPromotion: false, verbose: false)])
            }
        }
        
    }
}
