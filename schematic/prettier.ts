#!/usr/bin/env -S deno run --unstable --allow-read --allow-env --allow-write --allow-sys

import("npm:prettier/internal/cli.mjs").then(cli => cli.run([
    "--tab-width", "4",
    "--parser", "html",
    "--print-width", "2048"
]));
