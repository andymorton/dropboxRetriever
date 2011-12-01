echo off
setlocal enabledelayedexpansion
set cp=lib;config;.;./dropboxRetriever.jar


for %%f in (lib/*.jar) do (set cp=!cp!;lib/%%f%)


java -cp %cp% com.morty.dropbox.retriever.DropBoxRetrieverRunner %1 > %2
