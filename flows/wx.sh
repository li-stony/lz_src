#!/bin/bash
# before next ,
# must login both wechat and google,
# via exec to_sheets.py --login
# and wx-assistant.py ~/farm/wx 
# then they would store tokens , and never ask it
python wechat/wx-assistant.py ~/farm/wx | grep '|||' | proxychains4 python google/to_sheets.py --folder to_sheet --sheet wechat
