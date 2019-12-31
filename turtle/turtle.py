# 导入函数库
from jqdata import *

class Stock:
    def __init__(self,code, n):
        self.code = code
        self.last_price = 0.0
        self.buy_cnt = 0
        self.N = n
        self.days = 0
        
        self.gain = 0
        
        self.strat = 0

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
        #Stock('600519.XSHG',1),
        #Stock('000001.XSHE', 0.5),
        #Stock('600887.XSHG', 1),
        #Stock('600276.XSHG', 0.50),
        #Stock('601398.XSHG',0.15),
        #Stock('600999.XSHG',0.6),
        #Stock('600036.XSHG', 0.5),
        Stock('600030.XSHG',0.6),
        #Stock('600600.XSHG',2),
        Stock('600547.XSHG',0.5),
        #Stock('600435.XSHG', 0.2),
        #Stock('000835.XSHE',0.36),
        Stock('000603.XSHE', 0.30),
        Stock('600362.XSHG', 0.50),
        Stock('600028.XSHG', 0.50),
        Stock('600050.XSHG', 0.50),
        ]
    
    g.A_factor = 0.005
    g.A_min = 2000
    g.buy_max = 4
    g.day_max = 80
    
    g.stock_max = 12

    ### 股票相关设定 ###
    # 股票类每笔交易时的手续费是：买入时佣金万分之三，卖出时佣金万分之三加千分之一印花税, 每笔交易佣金最低扣5块钱
    set_order_cost(OrderCost(close_tax=0.001, open_commission=0.0003, close_commission=0.0003, min_commission=5), type='stock')

    
    ## 运行函数（reference_security为运行时间的参考标的；传入的标的只做种类区分，因此传入'000300.XSHG'或'510300.XSHG'是一样的）
      # 开盘前运行
    run_daily(before_market_open, time='before_open', reference_security='000300.XSHG')
      # 开盘时运行
    run_daily(market_open, time='open', reference_security='000300.XSHG')
      # 收盘后运行
    run_daily(after_market_close, time='after_close', reference_security='000300.XSHG')

## 开盘前运行函数
def before_market_open(context):
    # 输出运行时间
    log.info('函数运行时间(before_market_open)：'+str(context.current_dt.time()))
    

def buy_stock (stock, strat, unit, price):
    if strat == 1:
        if stock.gain == 1:
            # ignore this entry
            stock.gain = 0
            return
    order_value(stock.code, unit * price)
    stock.buy_cnt = stock.buy_cnt + 1
    stock.last_price = price
    stock.strat = strat
    
def buy_more(stock, unit, price):
    order_value(stock.code,  unit * price)
    stock.buy_cnt = stock.buy_cnt + 1
    stock.last_price = price
    
    
def sell_stock(stock, price):
    order_target(stock.code, 0)
    stock.buy_cnt = 0
    stock.last_price = 0
    stock.strat = 0
    if price > stock.last_price:
        stock.gain = 1
    else:
        stock.gain = 0


## 开盘时运行函数
def market_open(context):
    # log.info('函数运行时间(market_open):'+str(context.current_dt.time()))

    stock_cnt = 0
    for stock in g.security:
        # 获取股票的收盘价
        close_data = get_bars(stock.code, count=500, unit='1d', include_now=False, fields=['close'])
        
        # 取得上一时间点价格
        current_price = close_data['close'][-1]
        price_list = close_data['close']
        # print(price_list)
        
        day_buy = price_list[len(price_list)-20:]
        day_sell = price_list[len(price_list)-10:]
        new_high = max(day_buy)
        new_low = min(day_sell)
        
        day_buy2= price_list[len(price_list)-60:]
        day_sell2 = price_list[len(price_list)-20:]
        new_high2 = max(day_buy2)
        new_low2 = min(day_sell2)

         # 取得当前的现金
        cash = context.portfolio.available_cash
        total_value = context.portfolio.total_value
        A = max(total_value * g.A_factor, g.A_min)
        unit =  A / stock.N
        
        # filter
        # 过滤器：350日/25日指数移动平均趋势过滤器
        ma350 = price_list[-350:-1].mean()
        ma25 = price_list[-25:-1].mean()
        print(ma25, ma350)
        
        # don't open if year_mean_average downtrend
        # if (stock.buy_cnt == 0) and (ma200_10 > ma200_60):
        # if (stock.buy_cnt == 0) and (ma25 > ma350):
        if stock.buy_cnt == 0:
            # entry
            if ((new_high <= current_price) or (new_high2 <= current_price)):
            # if new_high2 <= current_price:
                if cash > (unit*current_price):
                    log.info('buy:'+ str(new_high))
                    if new_high <= current_price:
                        buy_stock(stock, 1, unit, current_price)
                    else:
                        buy_stock(stock, 2, unit, current_price)
            
        
        if stock.buy_cnt > 0:
            # check error
            if stock.strat == 0:
                log.error('error: strat is 0.' + stock.code)
                a = 5 / 0.0
                
            # exit
            if stock.strat == 1:
                if new_low >= current_price:
                    log.info('sell sys1:' + str(new_low))
                    sell_stock(stock, current_price)
            if stock.strat == 2:
                if new_low2 >= current_price:
                    log.info('sell sys2:' + str(new_low2))
                    sell_stock(stock, current_price)
            
            # stop loss
            if (stock.last_price - stock.N * 2 ) > current_price:
                log.info('- sell:' + str(current_price))
                sell_stock(stock, current_price)
                
            # stop loss 2
            avg_cost = context.subportfolios[0].long_positions[stock.code].acc_avg_cost
            if (current_price / avg_cost) < 0.98:
                log.info('-- sell:' + str(current_price))
                sell_stock(stock, current_price)
            
            # buy more
            if (stock.buy_cnt > 0) and (stock.buy_cnt < g.buy_max) and (current_price - stock.last_price) > (stock.N / 2):
                
                if cash > (unit*current_price):
                    log.info('+ buy:'+ str(new_high))
    
                    buy_more(stock, unit, current_price)
            
            
        if stock.buy_cnt > 0:
            stock_cnt = stock_cnt + 1
        if stock_cnt > g.stock_max:
            # don't hold so many stocks
            log.info('stock count:' + str(stock_cnt))
            break
   

## 收盘后运行函数
def after_market_close(context):
    log.info(str('函数运行时间(after_market_close):'+str(context.current_dt.time())))
    #得到当天所有成交记录
    trades = get_trades()
    for _trade in trades.values():
        log.info('成交记录：'+str(_trade))
    log.info('一天结束')
    log.info('##############################################################')