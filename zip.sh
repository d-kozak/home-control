#!/usr/bin/env bash
ZIP_NAME="dkozak.zip"
rm -f ${ZIP_NAME}
zip -r ${ZIP_NAME} client gateway imgs sensors server build.gradle log.properties README.md run.sh settings.gradle DOC.pdf -x "*/.git/**" -x "*/build/**" -x "*/.idea/**" -x "*/out/**"