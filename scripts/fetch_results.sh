#!/usr/bin/env bash
# Throwaway data-acquisition script for the 1974/75 week-10 dataset.
#
# Source history (2026-07-11):
#   1. englishfootballleaguetables.co.uk (primary plan) — entire site returns
#      502 and its /season/ date pages were never archived by the Wayback
#      Machine. Dead end, but its archived season calendar validated the
#      matchday date list.
#   2. 11v11.com, worldfootball.net, fbref.com — all return 403 to
#      non-browser clients and have no usable Wayback snapshots of the
#      pages we need.
#   3. statto.com (defunct since 2017) — well archived. Its
#      /division-one-old/1974-1975/results/<date> pages list ONLY First
#      Division matches for that date (division filtering is in the URL,
#      not in page content), and /table/full/1974-09-28 provides a
#      cross-check league table dated exactly at our cutoff.
#
# Dates: every First Division matchday on or before Sat 28 Sep 1974 for
# which Statto has a results page (14 dates — includes Mon 1974-08-19,
# which the original hand-researched list missed).
#
# Raw HTML is cached under data/raw/ (gitignored — we keep the script,
# not the scrapes). "id_" in Wayback URLs = unmodified original markup.
set -euo pipefail

cd "$(dirname "$0")/.."
mkdir -p data/raw

STATTO="http://www.statto.com/football/stats/england/division-one-old/1974-1975"
WAYBACK="http://web.archive.org/web/2012id_"

DATES=(
  1974-08-17 1974-08-19 1974-08-20 1974-08-21 1974-08-24
  1974-08-27 1974-08-28 1974-08-31
  1974-09-07 1974-09-14 1974-09-21 1974-09-24 1974-09-25 1974-09-28
)

for d in "${DATES[@]}"; do
  out="data/raw/statto-results-${d}.html"
  if [[ -s "$out" ]]; then
    echo "cached:  $out"
    continue
  fi
  # -L: Wayback redirects the approximate timestamp to the nearest snapshot
  curl -fsSL --retry 3 --retry-delay 2 --max-time 90 \
    -o "$out" "${WAYBACK}/${STATTO}/results/${d}" || {
      echo "MISSING: $d" >&2; rm -f "$out"; }
  sleep 1
done

table_out="data/raw/statto-table-full-1974-09-28.html"
if [[ ! -s "$table_out" ]]; then
  curl -fsSL --retry 3 --retry-delay 2 --max-time 90 \
    -o "$table_out" "${WAYBACK}/${STATTO}/table/full/1974-09-28"
fi

echo "done: $(ls data/raw/statto-*.html | wc -l | tr -d ' ') pages cached"
