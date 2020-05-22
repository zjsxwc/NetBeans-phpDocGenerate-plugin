
NetBeans PHP Doc 补全 插件


目前实现了

#### 对 /** 补全@var

比如原先是 

```
/**<光标位置>
$aaa =
```
补全后变成

```
/** @var <光标位置> $aaa */
$aaa =
```



#### 对 foreach 补全

比如原先是 

```
foreach ($aaaList as $aaa) {<光标位置>
```
补全后变成

```
/** @var AAAType $aaa */
foreach ($aaaList as $aaa) {<光标位置>
```

