# Bash Completion for `vmc`

This is a bash completion script for the Cloud Foundry command-line utility vmc.

### Installation

To install this script, you need to source it into your bash shell. There are a 
number of different ways to do this. On Linux, you can just add the "vmc.sh" file 
to your `/etc/bash_completion.d` folder:

    cd src/
    git clone git://github.com/SpringSource/cloudfoundry-samples.git
    cd cloudfoundry-samples/bash-completion
    cp vmc.sh /etc/bash_completion.d/vmc

Then when you start a new shell, you should have completion support for vmc.

On a Mac, you could just source it from your `.profile` script:

    vi ~/.profile
    (at the bottom, add:)
    . ~/src/cloudfoundry-samples/bash-completion/vmc.sh

Or you could get fancy and create a ~/.bash_completion.d directory and abscond with 
some code from the Ubuntu `/etc/bash_completion` script.

### Usage

This will offer completion for all the options like `--email` and `--password` as 
well as all the commands like `push`, `create-service`, and the like.

Some commands have additional completion support. For instance, doing:

    vmc create-service [tab][tab]

will give you a list of the available service names to provision. And:

    vmc bind-service [tab][tab]

will give you a list of the services you have provisioned which are available to be 
bound.

It will also complete on application names. So setting an environment variable is easier:

    vmc env [tab][tab]

will list the deployed applications.