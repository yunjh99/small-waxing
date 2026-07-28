#!/usr/bin/env bash

set -Eeuo pipefail

readonly APP_USER="ubuntu"
readonly APP_DIR="/home/ubuntu/small-waxing"
readonly HEALTH_URL="https://smallwaxing.com/"

run_as_app_user() {
  sudo -u "$APP_USER" -H "$@"
}

cd "$APP_DIR"

run_as_app_user git fetch --prune origin main
run_as_app_user git checkout main
run_as_app_user git merge --ff-only origin/main

/usr/bin/docker compose config --quiet
/usr/bin/docker compose up -d --build --remove-orphans
/usr/bin/docker compose ps

if ! /usr/bin/curl \
  --fail \
  --silent \
  --show-error \
  --location \
  --retry 12 \
  --retry-delay 5 \
  --retry-connrefused \
  "$HEALTH_URL" > /dev/null; then
  /usr/bin/docker compose logs --no-color --tail 100 app
  exit 1
fi

