#!/bin/bash

# Check if script is called from commandline or from within another script
if ps -o stat= -p $PPID | grep -q "s"; then
    source "$(dirname "${BASH_SOURCE[0]}")/../../config/scoring.sh"
fi

echo "Validating clean_after_scoring parameter..."
case "${clean_after_scoring}" in
    true )
        ;;   #fallthru
    false )
        ;;   #fallthru
    * )
        echo "Invalid clean_after_scoring parameter: ${clean_after_scoring}. Should be true or false"
        exit 1
esac
echo "  clean_after_scoring = ${clean_after_scoring}"

echo
echo "All input parameters are valid"
echo