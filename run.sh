#!/bin/sh
CLASSPATH=lib:.:./dropboxRetriever.jar
for i in lib/*.jar
do
CLASSPATH=$CLASSPATH:$i
done

echo $CLASSPATH

java -classpath $CLASSPATH com.morty.dropbox.retriever.DropBoxRetrieverRunner standard-context.xml
