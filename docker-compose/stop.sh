SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"

docker-compose   \
  -f "${SCRIPT_DIR}/network.yml" \
  -f "${SCRIPT_DIR}/kafka.yml" \
  -f "${SCRIPT_DIR}/elastic.yml" \
  -f "${SCRIPT_DIR}/services.yml" \
down