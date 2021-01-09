#!/bin/bash
# Updates the actual files for testing, based on the files received during the testing process.
# Replacement files must be obtained on the stable version of the crawler.
cp "systest.csv" "get_top_test_raw.csv"
cp "cmtest.csv" "act_cmtest.csv"
cp "prtest.csv" "act_prtest.csv"
cp "get_top_test_proc.csv" "act_get_top.csv"
cp "sys_top_test.csv" "act_sys_top_test.csv"
cp "systest.csv" "act_systest.csv"
cp "sttest.csv" "act_sttest.csv"
cp "ptest.csv" "act_ptest.csv"
cp "st_top_test.csv" "act_st_top_test.csv"
cp "sttest.csv" "act_sttest.csv"
cp "preptest.csv" "act_preptest.csv"
