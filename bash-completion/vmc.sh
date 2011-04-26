#   Copyright (c) 2011, SpringSource
#
#   Licensed under the Apache License, Version 2.0 (the "License");
#   you may not use this file except in compliance with the License.
#   You may obtain a copy of the License at
#
#       http://www.apache.org/licenses/LICENSE-2.0
#
#   Unless required by applicable law or agreed to in writing, software
#   distributed under the License is distributed on an "AS IS" BASIS,
#   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#   See the License for the specific language governing permissions and
#   limitations under the License.
   
_vmc() {
  local cur prev opts push_opts apps my_svcs
  COMPREPLY=()
  cur="${COMP_WORDS[COMP_CWORD]}"
  prev="${COMP_WORDS[COMP_CWORD-1]}"
  options="--email --user --passwd --pass --password --app --name --bind --instance --instances --url --mem --path --no-start --nostart --force --all -t --trace -q --quiet --no-zip --nozip --no-resources --noresources --no-color --verbose -n --no-prompt --noprompt --non-interactive --prefix --prefix-logs --prefixlogs --json -v --version -h --help --runtime --exec --noframework --canary -u --options"
  push_opts="--path --url --instances --mem --no-start"
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
      fi
      COMPREPLY=( $(compgen -W "${VCAP_AVAIL_SVCS}" -- ${cur}) )
      return 0
      ;;
    delete-service|bind-service|unbind-service)
      # Load provisioned services all the time so we don't miss one
      my_svcs=$( vmc services | sed -n '/^| Name.*/,$ s/^|[ ]\(.*\)[ +]|.*|$/\1/p' | sed -n '2,$ p' )
      COMPREPLY=( $(compgen -W "${my_svcs}" -- ${cur}) )
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