SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"

# shellcheck disable=SC2046
source "${SCRIPT_DIR}/.env"

docker-compose \
  --env-file "${SCRIPT_DIR}/.env" \
  -f "${SCRIPT_DIR}/network.yml" \
  -f "${SCRIPT_DIR}/kafka.yml" \
  -f "${SCRIPT_DIR}/services.yml" \
  up -d
