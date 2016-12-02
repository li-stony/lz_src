// data start
var rules = ["facebook","google"];
// data end

var direct = "DIRECT";
var proxy = "PROXY 127.0.0.1:1080"
function FindProxyForURL(url, host) {
    // console.log('find host:'+host);
    for(var prop in rules) {
        
        if(rules.hasOwnProperty(prop)){
            console.log(rules[prop])
            if(rules[prop] === host) {
                return proxy;
            }
            
        }
    }
    return direct;
}
// test start
var result = FindProxyForURL("www.google.com", "google.com");
console.log(result);
var result = FindProxyForURL("www.baidu.com/sss", "baidu.com");
console.log(result);
var result = FindProxyForURL("www.sogou.com/sss", "sogou.com")
console.log(result);
// test end