我有个需求，就是希望统计zip文件中某一层级的所有的文件和文件夹的大小。这个分析jar 包中每部分代码占比的时候非常有用。

自己使用第三方工具先试验了下，结果：
	7z 可以，在 ui 界面中显示。但是不能以 树的形式显示所有的，只能显示当前目录里的所有子目录和文件的大小。
	PowerShell 的 ZipFile 是把所有的文件列了出来，没办法显示某一文件夹的大小。也是需要自己编写代码才能满足需求。

那就“一言不合就自己写吧”。
正好，我在想怎么恢复一下自己 C++ 的能力。
用 C++ 写了个工程，没做跨平台适配。以后有需要再写 makefile吧。可以统计 zip 文件中各个层级的文件夹和文件大小。没有使用第三方库，而是直接解析zip文件，按照官方zip格式定义，目前不支持zip64格式。

参考
https://pkware.cachefly.net/webdocs/casestudies/APPNOTE.TXT
https://en.wikipedia.org/wiki/Zip_(file_format)#cite_note-29
