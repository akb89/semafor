#!/bin/bash

echo
echo "Validating input parameters..."
echo

echo "Validating dependency_parser parameter..."
case "${dependency_parser}" in
    MST )
        ;;   #fallthru
    MALT )
        ;;   #fallthru
    TURBO )
        ;;   #fallthru
    * )
        echo "Invalid dependency_parser parameter: ${dependency_parser}. Should be MST, MALT or TURBO"
        exit 1
esac
echo "  dependency_parser = ${dependency_parser}"

echo "Validating with_exemplars parameter..."
case "${with_exemplars}" in
    true )
        ;;   #fallthru
    false )
        ;;   #fallthru
    * )
        echo "Invalid with_exemplars parameter: ${with_exemplars}. Should be true or false"
        exit 1
esac
echo "  with_exemplars = ${with_exemplars}"

echo "Validating clean_after_preprocessing parameter..."
case "${clean_after_preprocessing}" in
    true )
        ;;   #fallthru
    false )
        ;;   #fallthru
    * )
        echo "Invalid clean_after_preprocessing parameter: ${clean_after_preprocessing}. Should be true or false"
        exit 1
esac
echo "  clean_after_preprocessing = ${clean_after_preprocessing}"

echo
echo "All input parameters are valid"
echo