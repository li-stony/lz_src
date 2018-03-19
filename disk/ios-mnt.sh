#!/bin/bash

# remove old
fusermount -u ~/mnt/ios
idevicepair pair

ifuse ~/mnt/ios/

ls ~/mnt/ios/
