> tplink_management
>
> 本意是想写一个java控制路由器的工具包，目前只有登录，获取wan状态信息两块， 其他部分原理同wan状态获取， 
>
> 大致思路流程： 读取配置信息（路由器ip和路由器密码）， 然后模拟登陆获取到stock（请求参数pwd需要用tp的加密方法）， 然后如同wan状态信息获取一样获取想要的信息或者其他操作， （我这个路由器不同的操作只有请求参数和返回数据不一样其他几乎一致）