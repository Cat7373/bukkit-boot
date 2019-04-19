../gradlew clean jar test publish \
	    -Dnexus.username="$nexusUsername" \
	    -Dnexus.password="$nexusPassword" \
	    -Dgpg.keyId="$gpgKeyId" \
	    -Dgpg.password="$gpgPassword" \
	    -Dgpg.secretKeyRingFile="$gpgSecretFileName"
