#!/bin/bash

# Import the certificate
cp /opt/jboss/InstallCert/* .
java InstallCert $OSO_ADDRESS << ANSWERS
1
ANSWERS

if [[ -z "${KEYSTORE_PASSWORD}" ]]; then
  KEYSTORE_PASSWORD="almighty"
fi

echo "Setting a password to the new keystore..."
keytool -storepasswd -new $KEYSTORE_PASSWORD -keystore jssecacerts -storepass changeit

echo "Exporting the certificate into the keystore..."
keytool -exportcert -alias $OSO_DOMAIN_NAME-1 -keystore jssecacerts -storepass changeit -file openshift-io.cer

if [ $KEYCLOAK_USER ] && [ $KEYCLOAK_PASSWORD ]; then
    echo "Adding a new user..."
    keycloak/bin/add-user-keycloak.sh --user $KEYCLOAK_USER --password $KEYCLOAK_PASSWORD
fi

echo "Starting keycloak-server..."

exec /opt/jboss/keycloak/bin/standalone.sh $@
exit $?
