
NetBeans PHP Doc 补全 插件


目前只实现了对 foreach 补全

比如原先是 

```
foreach ($aaaList as $aaa) {<光标位置>
```
补全后变成

```
/** @var AAAType $aaa */
foreach ($aaaList as $aaa) {<光标位置>
```

