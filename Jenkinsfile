pipeline{
     
    agent any
    stages{
         
        stage('Get surce from GIT'){
            steps{
               // Get some code from a GitHub repository...
               //This path must exists on jenkins server
               sh 'rm -rf /opt/cicd/java_ms_incrementa'
               sh 'git clone https://github.com/erdnando/coltrans-ms-incrementa.git /opt/cicd/java_ms_incrementa'
               echo 'Clon OK'
            }
         }
         
        stage('Build docker image'){
            steps{
                sh 'docker build -t erdnando/coltrans-ms-incrementa:1.0 /opt/cicd/java_ms_incrementa/'
                echo 'DockerBuild OK...'
            }
            
        }
        
         stage('Publish to DockerHub'){
            steps{
                 withDockerRegistry([ credentialsId: "github_erv", url: "" ]) {
                      sh 'docker push erdnando/coltrans-ms-incrementa:1.0'
                    }
               
                } 
            }  
    }
    
}
