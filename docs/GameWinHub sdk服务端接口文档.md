## GameWinHub sdk服务端接口文档



### 一、登录验证

游戏登陆的token验证(游戏方获取user_id和token后访问)

游戏服务器 --> 平台服务器：

测试环境：https://papi-stage.gamewinhub.com/sdk/login_notify/login_verify

生产环境：https://papi.gamewinhub.com/sdk/login_notify/login_verify

 

传值方式 POST(参数格式：json格式)

| 参数    | 类型   | 备注         |
| ------- | ------ | ------------ |
| user_id | string | 用户唯一标示 |
| token   | string | token        |

 

返回数据例子

```JSON
{
    "code":200,
    "msg":"SUCCESS"
}
```

code状态为200时 验证成功; 其它为失败

 

### 二、支付结果通知

平台服务器-->CP服务器

传值方式 POST(参数形式)

| 参数         | 类型   | 备注                                                       |
| ------------ | ------ | ---------------------------------------------------------- |
| game_order   | string | 下单时游戏所传透传参数（额外参数下单时拼接，回调原样返回） |
| out_trade_no | string | 平台方订单                                                 |
| pay_extra    | string | 平台方透传信息  （默认是平台方域名）                       |
| pay_status   | int    | 支付结果 固定值1                                           |
| price        | string | 订单金额 游戏方需验证金额是否和发起订单一致，单位美元      |
| user_id      | int    | 用户唯一标识  游戏方需验证是否和发起订单一致               |
| sign         | string | 加密字符串 签名规则如下                                    |

签名规则：

MD5(game_order+out_trade_no+pay_extra+pay_status+price+user_id+KEY)

KEY值：平台方与游戏方协商;

示例: MD5(\*\*\*\*KEY) 

(MD5请按照给出的顺序进行加密)

成功时 游戏方请返回`success`这7个字符 否则视为失败

 

 

**注意：**

平台只对充值成功的订单进行通知。

平台可能对某个订单重复通知，请勿重复处理；对已经成功处理的订单返回 `success` 避免重复通知。

游戏方接收到平台通知后，在验证签名的基础上还需要对price参数进行确认，支付金额是否对应订单的实际金额，防止以较少金额购买游戏货币;需要对user_id参数进行确认。

已经给用户发放过的游戏币订单也请返回成功，但是不要重复给用户发放游戏币。（平台会对成功的订单校验是否成功，可能出现重复回调情况）