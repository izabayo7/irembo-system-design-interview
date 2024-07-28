#!/bin/sh

cat << EOT >> .git/hooks/pre-push
#!/bin/sh
mvn clean verify
EOT
chmod +x .git/hooks/pre-push
echo -e "Successfully setup test verification in pre-push hook"
