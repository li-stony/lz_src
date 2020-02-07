#
# 虽然没有加仓，但是加入 ATR 后，效果也好了很多啊！
# 从2005年测试，比固定 N 的测试要好很多！
# 所以，精髓应该是资金管理。毕竟趋势追踪总是会入场的。
# 也可以说，只要是活着，只要是轻仓试错，让利润奔跑，那么总能赚到钱。
# 而多赚钱的原因是，使用 ATR 这样的比较科学的分散方法。
# 导入函数库
from jqdata import *

class Stock:
    def __init__(self,code):
        self.code = code
        self.last_price = 0.0
        self.buy_cnt = 0
        self.days = 0
        self.profit = 0
        
    def buy(self, unit, price):
        re = order_value(self.code, unit * price)
        if re is not None:
            self.buy_cnt = self.buy_cnt + 1
            self.last_price = price
        
        return re
        
    def sell(self, price):
        re = order_target(self.code, 0)
        self.buy_cnt = 0
        self.last_price = 0
        self.days = 0
        if price > self.last_price * 1.02:
            profit = 1
            
            
    
    def inc_days(self):
        self.days = self.days + 1

def atr(low, high, close):
    cnt = 20
    size = len(close)
    
    sum = 0
    for i in range(size-cnt-1, size-1):
        sum = sum + max(high[i+1]-low[i+1], close[i]-low[i+1], high[i+1]-close[i])
    
    tr = sum / cnt
    return tr
    

# 初始化函数，设定基准等等
def initialize(context):
    # 设定沪深300作为基准
    set_benchmark('000300.XSHG')
    # set_benchmark('600887.XSHG')
    # 开启动态复权模式(真实价格)
    set_option('use_real_price', True)
    # set_option('use_real_price', False)
    # 输出内容到日志 log.info()
    log.info('初始函数开始运行且全局只运行一次')
    # 过滤掉order系列API产生的比error级别低的log
    # log.set_level('order', 'error')

    g.security = [
        # Stock('000001.XSHE'),
        # Stock('600887.XSHG'),
        Stock('600999.XSHG'),
        Stock('600030.XSHG'),
        Stock('600600.XSHG'),
        Stock('600547.XSHG'),
        Stock('600435.XSHG'),
        # Stock('000835.XSHE'),
        #Stock('002600.XSHE'),
        Stock('000603.XSHE'),
        Stock('600362.XSHG'),
        Stock('600028.XSHG')
        ]

    g.factor = 0.01
    
    ### 股票相关设定 ###
    # 股票类每笔交易时的手续费是：买入时佣金万分之三，卖出时佣金万分之三加千分之一印花税, 每笔交易佣金最低扣5块钱
    set_order_cost(OrderCost(close_tax=0.001, open_commission=0.0003, close_commission=0.0003, min_commission=5), type='stock')

    run_daily(market_open, time='open', reference_security='000300.XSHG')


## 开盘时运行函数
def market_open(context):
    cash = context.portfolio.available_cash
    total_value = context.portfolio.total_value
    
    
    for stock in g.security:

        datas = get_bars(stock.code, count=60, unit='1d', include_now=False, fields=['close', 'high', 'low'])
        
        close_list = datas['close']
        
        if len(close_list) < 30:
            continue
        current_price = datas['close'][-1]
        
        
        high_list = datas['high']
        #high_list = datas['close']
        low_list = datas['low']
        # print(price_list)
        
        day_buy = []
        day_buy.extend(high_list[-20:])

        day_sell = []
        day_sell.extend(low_list[-10:])
        
        new_high = max(day_buy)
        new_low = min(day_sell)


         # 取得当前的现金
        cash = context.portfolio.available_cash

        if stock.buy_cnt == 0 and new_high <= current_price:
            
            if stock.profit == 0:
                #atr
                a = atr(low_list, high_list, close_list)
                unit = total_value * g.factor / a
                unit = unit // 100 * 100
                stock.buy( unit, current_price + 0.02)
            else:
                stock.profit = 0
        
        if stock.buy_cnt > 0 and new_low >= current_price:
            stock.sell(current_price)
            # cash = context.portfolio.available_cash
            # inout_cash(-1 *cash // 2 ,0)
                    

    
   
