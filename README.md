# TextLineCollector
代码统计工具

----

### help

```
C:\Users\lanyj\Desktop\tstSrc>java -jar colc.jar -h
Gitignore line collector, https://github.com/lanyj/TextLineCollector
-i: input file, default ".gitignore"
-ni: no input file, for colc all files
-d: output detail, default false
-h: help doc
```

### default using `.gitignore` file as input

```
C:\Users\lanyj\Desktop\tstSrc>java -jar colc.jar
------------------------------------------------
Colc on 'C:\Users\lanyj\Desktop\tstSrc\'
------------------------------------------------
txt file count     =           32
txt line count     =         1125
txt file size      =        31723
------------------------------------------------
bin file count     =           24
bin file size      =    27.8096 K
------------------------------------------------
```

### using `-i path` to specific input file

```
C:\Users\lanyj\Desktop\tstSrc>type colc.txt
.settings/
bin/
target/
.git/

C:\Users\lanyj\Desktop\tstSrc>java -jar colc.jar -i "colc.txt"
------------------------------------------------
Colc on 'C:\Users\lanyj\Desktop\tstSrc\'
------------------------------------------------
txt file count     =           10
txt line count     =          455
txt file size      =        11545
------------------------------------------------
bin file count     =            1
bin file size      =    11.1357 K
------------------------------------------------
```

### using `-d` for log detail

```
C:\Users\lanyj\Desktop\tstSrc>java -jar colc.jar -i "colc.txt" -d
Patterns
^\.settings[/\\].*$
^bin[/\\].*$
^target[/\\].*$
^\.git[/\\].*$

[COLC] <TEXT>,              6,            ".classpath"
[PASS] <^\.git[/\\].*$                  > ".git\"
[COLC] <TEXT>,              2,            ".gitignore"
[COLC] <TEXT>,             17,            ".project"
[PASS] <^\.settings[/\\].*$             > ".settings\"
[PASS] <^bin[/\\].*$                    > "bin\"
[COLC] <BINY>,      11.1357 K,            "colc.jar"
[COLC] <TEXT>,              4,            "colc.txt"
[COLC] <TEXT>,              9,            "src\cn\lanyj\colc\core\interfaces\Filter.java"
[COLC] <TEXT>,             33,            "src\cn\lanyj\colc\core\interfaces\impl\PatternFilter.java"
[COLC] <TEXT>,             63,            "src\cn\lanyj\colc\git\FileHelper.java"
[COLC] <TEXT>,             17,            "src\cn\lanyj\colc\git\GitignoreFilter.java"
[COLC] <TEXT>,            246,            "src\cn\lanyj\colc\git\GitignoreLauncher.java"
[COLC] <TEXT>,             58,            "src\cn\lanyj\colc\git\GitignoreProcessor.java"
------------------------------------------------
Colc on 'C:\Users\lanyj\Desktop\tstSrc\'
------------------------------------------------
txt file count     =           10
txt line count     =          455
txt file size      =        11545
------------------------------------------------
bin file count     =            1
bin file size      =    11.1357 K
------------------------------------------------

```
