#!/usr/bin/env bash

print_options() {
	echo 'Available commands:'
	echo '-n, --iterations <iterations number> -- set number of iterations(100 by deafult) for each test except Ackerman function'
	echo '--ack_it <iterations number> -- set number of iterations(0 by default) for Ackerman function test'
	echo '-o, --output <ouput dir> -- set log drectory(./log by default)'
	echo '-h, --help -- view list of available commands'
}

#-------------------------------------------------

ITERATIONS=100
ACK_ITERATIONS=0
LOG_PATH=log
RUN=true

while [[ $# > 1 ]]
do
	key="$1"
	echo "key = $key"
	case $key in 
		-n|--iterations)
			ITERATIONS="$2"
			shift
		;;
		--ack_it)
			ACK_ITERATIONS="$2"
			shift
		;;
		-o|--output)
			LOG_PATH="$2"
			shift
		;;
		-h|--help)
			print_options
		;;
		*)
			echo "unknown parameter $key"
			RUN=false
		;;
	esac
	shift
done

if [[ $# == 1 ]];
then
	case "$1" in 
		-h|--help)
			print_options
		;;
		*)
			echo "unknown parameter $1"
			RUN=false
		;;
	esac
	shift
fi

if [[ RUN == false ]];
then
	exit -1
fi

TIME=/usr/bin/time
TEST_PATH=`pwd`
TEST_EMLUA=$TEST_PATH/emlua.js

#-----------------------------build emlua if necessary
if [ ! -f $TEST_EMLUA ];
then 
	EMLUA_PATH=../lua/src
	EMLUA=$EMLUA_PATH/emlua.js

	cd $EMLUA_PATH
	bash ./build.sh
	cd $TEST_PATH
	$(cat $EMLUA $TEST_PATH/testfunc.js > $TEST_EMLUA)
	$(cat $EMLUA $TEST_PATH/browser/browser_testfunc.js > $TEST_PATH/browser/browser_emlua.js)

fi
#-------------------------------------


#-----------------------------back up existing log directory if necessary
if [ ! -d $LOG_PATH ];
then
	rm -rf $LOG_PATH 2> /dev/null
	mkdir $LOG_PATH
else
	mv $LOG_PATH ${LOG_PATH}`stat -c %Y ${LOG_PATH}`
	mkdir $LOA_PATH
fi
#-------------------------------------

for tst in `ls -1 src`;
do
	if [ $tst == ack.lua ];
	then
		its=$ACK_ITERATIONS
	else
		its=$ITERATIONS
	fi

	logfile=$LOG_PATH/${tst//lua/log}
	echo '{' >> $logfile
	
	for i in `seq 1 $its`;
	do

		echo "running test ${tst//.lua/}. Iteration $i of $its"
		
		echo "on lua"

		echo "{'iteration': $i," >> $logfile 
		echo "'executor': 'lua'," >> $logfile
		$($TIME -f "'time': %e,\n'mem': %M}," -a -o $logfile lua src/$tst)

		echo "on emlua"

		echo "{'iteration': $i," >> $logfile
		echo "'executor': 'emlua'," >> $logfile
		$($TIME -f "'time': %e,\n'mem': %M}," -a -o $logfile nodejs $TEST_EMLUA src/$tst >> $logfile 2> /dev/null)
	done
	echo '}' >> $logfile
done
