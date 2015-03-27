::- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
::- Università degli Studi di Pisa 
::- A.A. 2012/2013
::- Progetto di Reti dei Calcolatori e Laboratorio (RCL)
::- 
::- Progetto: Reduced Dynamo
::- Sviluppatore: Lovagnini Luca
::- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

::- - - - - - - - - - - - - - - - - - MAKEFILE - - - - - - - - - - - - - - - - - - - - -


::- - - - - - - - - - - - - - - - - - - MACRO - - - - - - - - - - - - - - - - - - - - - -

::cartella di destinazione per i file compilati (.class)
SET CLASSFOLDER=classes

::cartella contente tutti i file del progetto (.java)
SET SOURCEPATH=src

::cartella di destinazione dei file jar
SET JARFOLDER=jar

::opzioni di compilazione
SET SW=-g -verbose -target 1.6

:: compilatore
SET JC=javac

:: - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
 
:: default target- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

	echo 'Compilazione Progetto...'
	
	%JC% %SW% -d %CLASSFOLDER% -sourcepath %SOURCEPATH% -classpath %CLASSFOLDER% %SOURCEPATH%\*.java


	echo 'Creazione jar BootstrapServer'
	
	jar cvfe %JARFOLDER%\BootstrapServer.jar BootstrapServerMain  -C classes .
	
	
	echo 'Creazione jar StorageNode_Client'
	
	jar cvfe %JARFOLDER%\StorageNode_Client.jar StorageNode_Client_Main  -C classes .
	
	
	echo 'Creazione jar Simulatore'
		
	jar cvfe %JARFOLDER%\Simulatore.jar  Simulatore  -C classes .
	
	echo 'Compilazione file .java e creazione file jar completata'
	
	pause
	