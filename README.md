# CSE2410_spring2016_projectileDysfunction

luke’s first commit

hello from Kels




BUILDING ELASTIC SEARCH!

Elastic search uses gradle to compile. In order to build the project, you will have to install 
gradle alongside the JDK. 

INSTALLING GRADLE (Mac OS X instructions).

On the Terminal, check the version of your JDK by entering the command:

    javac -version

If you recieve an error message, you will have to ensure that JDK or JRE is installed.

Go to http://gradle.org/gradle-download/ to download gradle (the first option, "complete distribution", is 
recommended). Unzip the file.

At this point, ensure you're on the admin user directory on the terminal. The current directory on your 
terminal should look like this (using mine as example.): host37-163:~ ogbonnayacngwu$ 
The "~" should be before your name 

Enter the following command on your terminal:

    mkdir gradle

Go to finder, open this folder. Copy the unzipped downloaded gradle folder (it should have this name
gradle-2.10) and paste it in the gradle folder. 

On the terminal, enter this command:

    export PATH=/usr/bin:/bin:/usr/sbin:/sbin:/usr/local/bin:/usr/X11/bin

then enter:

    $PATH 

If you do not see the above PATH, enter:

    echo $PATH

if you do not see it, try exporting it again. 
Alternatively (I don't recommend doing this), you can enter it by opening the bash profile using the command:
    touch ~/.bash_profile; open ~/.bash_profile
        
!!!(avoid doing this)!!!To force the .bash_profile to execute, use: source ~/.bash_profile

if you saw the PATH, after using the PATH diaplayer above, enter the following command (Again, I'm using my 
own directory as example, ensure you use the actual ones for yours):

    export PATH=/Users/ogbonnayacngwu/gradle/gradle-2.10/bin:$PATH

To ensure the PATH was added, use the "$PATH" or "echo $PATH"

Make sure the PATH was added before you proceed.

You will then have to add JAVA_HOME to the location of your JDK. You will have to see where your JDK is installed.
For instance mine is installed in the Libraries/Java/JavaVirtualMachines/jdk1.8_11.jdk/Contents/Home. Hence, 
I will link my JAVA_HOME to the location using the command:

    export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_11.jdk/Contents/Home

Now, you can check if gradle is linked using the command:
    
    gradle -version

You should see the following output:

------------------------------------------------------------
Gradle 2.10
------------------------------------------------------------

Build time:   2015-12-21 21:15:04 UTC
Build number: none
Revision:     276bdcded730f53aa8c11b479986aafa58e124a6

Groovy:       2.4.4
Ant:          Apache Ant(TM) version 1.9.3 compiled on December 23 2013
JVM:          1.8.0_11 (Oracle Corporation 25.11-b03)
OS:           Mac OS X 10.11.2 x86_64

If you recieve the folloeing error message, you will have to get the right location for JDK and export JAVA_HOME 
again.

ERROR: JAVA_HOME is set to an invalid directory: /Users/Java/JavaVirtualMachines/jdk1.8.0_11

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation.

To build and run the project; navigate to the project directory using change directory on the terminal.
Enter this command:

    gradle run

It will download all the plugin required by the jar files, then it will build the project and run it!

***Currently, mine failed to build. It throws an error on one if the file in elastic search.***


