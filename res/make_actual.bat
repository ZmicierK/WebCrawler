rem Updates the actual files for testing, based on the files received during the testing process. 
rem Replacement files must be obtained on the stable version of the crawler.
copy "systest.csv" "get_top_test_raw.csv"
copy "cmtest.csv" "act_cmtest.csv"
copy "prtest.csv" "act_prtest.csv"
copy "get_top_test_proc.csv" "act_get_top.csv"
copy "sys_top_test.csv" "act_sys_top_test.csv"
copy "systest.csv" "act_systest.csv"
copy "sttest.csv" "act_sttest.csv"
copy "ptest.csv" "act_ptest.csv"
copy "st_top_test.csv" "act_st_top_test.csv"
copy "sttest.csv" "act_sttest.csv"
copy "preptest.csv" "act_preptest.csv"