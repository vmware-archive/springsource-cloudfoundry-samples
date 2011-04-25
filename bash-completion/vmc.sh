_vmc() {
  local cur prev opts apps avail_svcs
  COMPREPLY=()
  cur="${COMP_WORDS[COMP_CWORD]}"
  prev="${COMP_WORDS[COMP_CWORD-1]}"
  options="--email --user --passwd --pass --password --app --name --bind --instance --instances --url --mem --path --no-start --nostart --force --all -t --trace -q --quiet --no-zip --nozip --no-resources --noresources --no-color --verbose -n --no-prompt --noprompt --non-interactive --prefix --prefix-logs --prefixlogs --json -v --version -h --help --runtime --exec --noframework --canary -u --options"
  commands="target login info apps push start stop restart delete rename update mem map unmap instances crashes crashlogs logs files stats instances env env-add env-del services create-service delete-service bind-service unbind-service clone-services user passwd logout add-user delete-user runtimes frameworks aliases unalias targets help"

  # Try to complete commands
  if [[ ${COMP_CWORD} -ge 1 && "${prev}" != -* ]] ; then
      COMPREPLY=( $(compgen -W "${commands}" -- ${cur}) )
  fi
  
  # Complete based on the user's deployed applications or services
  case "${prev}" in
    push|start|stop|restart|delete|rename|update|mem|map|unmap|instances|crashes|crashlogs|logs|files|stats|instances|env|evn-add|env-del)
      apps=$( vmc apps | sed -n '5,$ s/^|[ ]\(.*\)[ +]|\(.*|\)\{4,\}$/\1/p' )
      COMPREPLY=( $(compgen -W "${apps}" -- ${cur}) )
      ;;
    create-service)
      # Only load the list of available services once
      if [[ "${VCAP_AVAIL_SVCS}" == "" ]]; then
        export VCAP_AVAIL_SVCS=$( vmc services | sed -n '7,/Provisioned/ s/^|[ ]\(.*\)[ +]|\(.*|\)\{2,\}$/\1/p' )
        export VCAP_MY_SVCS=$( vmc services | sed -n '/^| Name.*/,$ s/^|[ ]\(.*\)[ +]|.*|$/\1/p' | sed -n '2,$ p' )
      fi
      COMPREPLY=( $(compgen -W "${VCAP_AVAIL_SVCS}" -- ${cur}) )
      return 0
      ;;
    delete-service|bind-service|unbind-service)
      if [[ "${VCAP_MY_SVCS}" == "" ]]; then
        export VCAP_MY_SVCS=$( vmc services | sed -n '/^| Name.*/,$ s/^|[ ]\(.*\)[ +]|.*|$/\1/p' | sed -n '2,$ p' )
      fi
      COMPREPLY=( $(compgen -W "${VCAP_MY_SVCS}" -- ${cur}) )
      ;;
  esac

  # If binding/unbinding a service, prompt for apps after the service
  if [[ ${#COMP_WORDS[@]} -gt 2 ]] ; then
    case "${COMP_WORDS[COMP_CWORD-2]}" in
      bind-service|unbind-service)
        apps=$( vmc apps | sed -n '5,$ s/^|[ ]\(.*\)[ +]|\(.*|\)\{4,\}$/\1/p' )
        COMPREPLY=( $(compgen -W "${apps}" -- ${cur}) )
        return 0
        ;;
      push)
        push_opts="--path --url --instances --mem --no-start"
        COMPREPLY=( $(compgen -W "${push_opts}" -- ${cur}) )
        return 0
        ;;
    esac
  fi
  
  # If creating a service, prompt for app name
  if [[ ${#COMP_WORDS[@]} -gt 3 ]] ; then
    case "${COMP_WORDS[COMP_CWORD-3]}" in
      create-service)
        apps=$( vmc apps | sed -n '5,$ s/^|[ ]\(.*\)[ +]|\(.*|\)\{4,\}$/\1/p' )
        COMPREPLY=( $(compgen -W "${apps}" -- ${cur}) )
        ;;
    esac
  fi
  
  # Try to complete options
  if [[ ${cur} == -* ]] ; then
      COMPREPLY=( $(compgen -W "${options}" -- ${cur}) )
      return 0
  fi

}
complete -F _vmc vmc