

console.log('Hello, world');

var page = require('webpage').create();


function load_index(status) {
    if(status != 'success'){
        console.log("Failed to load page")

    } else {
        console.log(status);
    }
    //phantom.exit();
}

function on_console(msg) {
    console.log(msg);
}

function on_response(response) {
    console.log("-- http response --") ;
    console.log(JSON.stringify(response, undefined, 4));
}

page.onResourceReceived = on_response;

page.open("http://www.newsmth.net/nForum/index",  load_index);

