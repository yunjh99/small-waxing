# Production deployment setup

The GitHub Actions workflow runs tests for pull requests. A successful push to
`main` connects to the Lightsail instance with a deployment-only SSH key. That
key is restricted to the root-owned deployment script below and cannot start a
general SSH shell.

## One-time Lightsail setup

Run these commands from the repository directory on the Lightsail instance:

```bash
cd ~/small-waxing
git pull --ff-only origin main
sudo install -o root -g root -m 0755 \
  ops/deploy-small-waxing.sh \
  /usr/local/sbin/deploy-small-waxing
```

Create a dedicated key pair:

```bash
ssh-keygen -t ed25519 \
  -C "github-actions-small-waxing" \
  -f "$HOME/github-actions-small-waxing" \
  -N ""
```

Authorize only the fixed deployment command:

```bash
printf 'command="sudo -n /usr/local/sbin/deploy-small-waxing",restrict %s\n' \
  "$(cat "$HOME/github-actions-small-waxing.pub")" \
  >> "$HOME/.ssh/authorized_keys"
chmod 700 "$HOME/.ssh"
chmod 600 "$HOME/.ssh/authorized_keys"
```

Copy the private key into the GitHub `production` environment secret named
`LIGHTSAIL_SSH_KEY`. Do not post or screenshot its output:

```bash
cat "$HOME/github-actions-small-waxing"
```

After saving the secret, remove the private and public deployment key files
from the server. The public key remains in `authorized_keys`:

```bash
rm "$HOME/github-actions-small-waxing" \
   "$HOME/github-actions-small-waxing.pub"
```

Create the other `production` environment secrets:

- `LIGHTSAIL_HOST`: the Lightsail static IPv4 address
- `LIGHTSAIL_USER`: `ubuntu`
- `LIGHTSAIL_KNOWN_HOSTS`: one line containing the static IPv4 address, a
  space, and the content of `/etc/ssh/ssh_host_ed25519_key.pub`

Generate the `LIGHTSAIL_KNOWN_HOSTS` value on the server by replacing
`STATIC_IP` with the actual static IPv4 address:

```bash
printf 'STATIC_IP %s\n' \
  "$(sudo cat /etc/ssh/ssh_host_ed25519_key.pub)"
```

## GitHub repository protection

Create a ruleset for the default branch (`main`) with these rules:

- Require a pull request before merging
- Require status checks to pass before merging
- Block force pushes
- Block deletions

After the workflow has run once, select the `Test` status check as required.
Configure the `production` environment so only `main` can deploy.

When all server setup and secrets are complete, create the repository variable
`ENABLE_PRODUCTION_DEPLOY` with the value `true`. Until this variable is set,
pushes to `main` run tests but skip production deployment.
