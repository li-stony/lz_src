import httplib2
import os
import sys

from apiclient import discovery
from oauth2client import client
from oauth2client import tools
from oauth2client.file import Storage


try:
    import argparse

    # default not open the web browser
    sys.argv.append('--noauth_local_webserver')

    parser = argparse.ArgumentParser(parents=[tools.argparser])
    # parser = argparse.ArgumentParser()
    parser.add_argument('--sheets', default='sheets', help='Sheets file name. example: to_sheet/weather')
    parser.add_argument('--login', default='', action='store_true', help='First time, login with google account.')
    flags = parser.parse_args()
except ImportError:
    flags = None

# If modifying these scopes, delete your previously saved credentials
# at ~/.credentials/sheets.googleapis.com-python-quickstart.json
SCOPES = ['https://www.googleapis.com/auth/spreadsheets', 'https://www.googleapis.com/auth/drive']
CLIENT_SECRET_FILE = 'to_sheets_key.json'
APPLICATION_NAME = 'to sheets'


def get_credentials():
    """Gets valid user credentials from storage.

    If nothing has been stored, or if the stored credentials are invalid,
    the OAuth2 flow is completed to obtain the new credentials.

    Returns:
        Credentials, the obtained credential.
    """
    home_dir = os.path.expanduser('~')
    credential_dir = os.path.join(home_dir, '.to_sheets')
    if not os.path.exists(credential_dir):
        os.makedirs(credential_dir)
    credential_path = os.path.join(credential_dir,
                                   'credential.json')

    store = Storage(credential_path)
    credentials = store.get()
    if not credentials or credentials.invalid:
        script_path = os.path.dirname(os.path.realpath(__file__))
        flow = client.flow_from_clientsecrets(os.path.join(script_path,CLIENT_SECRET_FILE), SCOPES)
        flow.user_agent = APPLICATION_NAME
        if flags:
            credentials = tools.run_flow(flow, store, flags)
        else: # Needed only for compatibility with Python 2.6
            credentials = tools.run(flow, store)
        print('Storing credentials to ' + credential_path)
    return credentials


def get_file_by_parent(drive_service, file_name, parent, is_folder):
    sql = "'{}' in parents".format(parent)
    if is_folder:
        sql = sql + ' and ' + "mimeType='application/vnd.google-apps.folder'"
    else:
        sql = sql + ' and ' + "mimeType='application/vnd.google-apps.spreadsheet'"
    # print(sql)
    # https://developers.google.com/resources/api-libraries/documentation/drive/v3/python/latest/drive_v3.files.html#list
    results = drive_service.files().list(
        q=sql).execute()

    items = results['files']
    for file in items:
        if file['name'] == file_name:
            return file['id']

    # create new one
    body = dict()
    body['name'] = file_name
    if is_folder:
        body['mimeType'] = 'application/vnd.google-apps.folder'
    else:
        body['mimeType'] = 'application/vnd.google-apps.spreadsheet'

    body['parents'] = [parent]
    #
    print('create file:', body)
    results = drive_service.files().create(body=body).execute()

    return results['id']


def get_file(drive_service, file_path):
    files = file_path.split('/')
    folder_level = 'root'
    for i in range(len(files) - 1):
        if len(files) == 0:
            continue
        folder_id = get_file_by_parent(drive_service, files[i], folder_level, True)
        if folder_id is None:
            return None

        folder_level = folder_id
    
    # get file
    file_id = get_file_by_parent(drive_service,  files[len(files)-1], folder_level, False)
    return file_id


def main(file_name):
    """

    :param folder: only 1-level folder!
    :param file_name:
    :return:
    """
    # got creadentials first
    credentials = get_credentials()
    http = credentials.authorize(httplib2.Http())

    # find the file before writing
    drive_service = discovery.build('drive', 'v3', http=http)

    file_id = get_file(drive_service, file_name)
    if file_id is None:
        return

    sheets_service = discovery.build('sheets', 'v4', http=http)

    results = sheets_service.spreadsheets().values().get(spreadsheetId=file_id, range='A:Z').execute()
    values = results['values']
    # write to stdout
    for arr in values:
        print('|', end='')
        for a in arr:
            print(a,end='|')
        print('')
    


if __name__ == '__main__':
    if vars(flags)['login']:
        get_credentials()
    else:
        main(vars(flags)['sheets'])
