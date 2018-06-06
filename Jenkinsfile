node {
    stage('Scale Down') {
        openshiftScale(deploymentConfig: 'cap-springboot-v3',replicaCount: '0')
    }
    stage('Build') {
        // echo 'openshiftBuild'
        openshiftBuild(buildConfig: 'cap-springboot-v3')
    }
    stage('Tag Image'){
        openshiftTag alias: 'false', apiURL: '', authToken: '', destStream: 'cap-springboot-v3', destTag: 'stage', destinationAuthToken: '', destinationNamespace: '', namespace: '', srcStream: 'cap-springboot-v3', srcTag: 'latest', verbose: 'false'
    }
    stage('Deploy') {
        openshiftDeploy(deploymentConfig: 'cap-springboot-v3')
    }
    stage('Scale Up') {
        openshiftScale(deploymentConfig: 'cap-springboot-v3',replicaCount: '1')
    }
    stage('Verify Service'){
        openshiftVerifyService apiURL: '', authToken: '', namespace: '', svcName: 'cap-springboot-v3', verbose: 'false'
    }
    stage('Veriry Deployment'){
        openshiftVerifyDeployment apiURL: '', authToken: '', depCfg: 'cap-springboot-v3', namespace: '', replicaCount: '', verbose: 'false', verifyReplicaCount: 'false', waitTime: '', waitUnit: 'sec'
    }
}