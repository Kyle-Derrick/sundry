/**
 * 时间段工具类,目前包含二分法按序插入和二分法查询功能
 */
class DateBucketUtil {
    //一天的时间戳差值(精确为秒)
    static DAY = 86400;
    arr = [];

    constructor(arr) {
        if (arr) {
            this.arr = arr;
        }
    }

    /**
     * 二分法排序插入
     * @param data 时间段数据(带优先级)
     */
    insert(data) {
        //处理时间段数据
        DateBucketUtil.dateBucketHandler(data);
        //验证数据是否正确
        if (!DateBucketUtil.verify(data)) {
            return;
        }
        //待处理队列
        let cursors = [new Cursor(this.arr, data)];
        while (cursors.length > 0) {
            //从待处理队列中取出一个任务
            let cursor = cursors.pop();
            //取出任务携带数据
            let item = cursor.data;

            //二分法循环体
            while (cursor.hasNext()) {
                //设置当前数组索引值
                cursor.setIndex();

                //获取数组当前位置的值
                let value = cursor.value();
                //数组中当前位置值和待插入数据是否有交集, 有则继续往下, 否则跳过当前循环
                if (cursor.move(DateBucketUtil.notCross(value, item))) {
                    continue;
                }
                //待插入值优先级是否高于数组当前位置值
                let itemHight = value.priority < item.priority;
                //A,B临时暂存数据, A表示优先级高数据, B表示低数据
                let dataA = value, dataB = item;
                if (itemHight) {
                    dataA = item;
                    dataB = value;
                }
                /*
                    假设优先级低数据的时间段区间包含优先级高数据的时间段区间,
                    然后得出左,中,右三个时间段数据,左和右为优先级低的数据减去优先级高数据后的拆分数据
                    中 表示高优先级值
                 */
                let leftTmp = Object.assign({}, dataB);
                leftTmp.endDate = dataA.beginDate - DateBucketUtil.DAY;
                let centerTmp = Object.assign({}, dataA);
                let rightTmp = Object.assign({}, dataB);
                rightTmp.beginDate = dataA.endDate + DateBucketUtil.DAY;
                /*
                    如果待插入数据优先级高,
                    左中右值分别表示
                        数组当前位置值相对于待插入值左边部份相差区间, 若数据无效,则表示待插入值左边界超出当前值左边界
                        插入值
                        数组当前位置值相对于待插入值右边部份相差区间, 若数据无效,则表示待插入值右边界超出当前值右边界
                 */
                if (itemHight) {
                    /*
                        如果拆分出的左右区间都为有效值(即区间值为开始时间小于等于结束时间),
                        则表示待插入数据区间包含在数组中当前位置数据中, 不过由于待插入值优先级高,
                        所以将依次插入拆分后的左中右值
                     */
                    if (DateBucketUtil.verify(leftTmp) && DateBucketUtil.verify(rightTmp)) {
                        cursor.splice(1, leftTmp, centerTmp, rightTmp)
                            .end();
                    }
                    /*
                        如果拆分出的左右区间都为无效值(即区间值为开始时间大于结束时间),
                        则表示待插入数据区间包含了数组中当前位置数据, 不过由于待插入值优先级高,
                        所以将删除数组中当前位置数据,
                     */
                    else if (!DateBucketUtil.verify(leftTmp) && !DateBucketUtil.verify(rightTmp)) {
                        cursor.splice(1)
                            .direction = -1;
                    }
                    /*
                        如果拆分出的左右区间只有一个为有效值,
                        则表示待插入数据区间与数组中当前位置数据相交, 不过由于待插入值优先级高,
                        所以将删除数组中当前位置数据中与待插入数据重叠部分, 然后待插入值进入下一循环,
                     */
                    else {
                        if (DateBucketUtil.verify(leftTmp)) {
                            cursor.splice(1, leftTmp)
                                .toRight();
                        } else {
                            cursor.splice(1, rightTmp)
                                .toLeft();
                        }
                    }
                }
                /*
                    如果待插入数据优先级低,
                    左中右值分别表示
                        待插入数据相对于数组当前位置值左边部份相差区间, 若数据无效,则表示数组当前位置值左边界超出待插入值左边界
                        数据当前位置值
                        待插入数据相对于数组当前位置值右边部份相差区间, 若数据无效,则表示数组当前位置值右边界超出待插入值左边界
                 */
                else {
                    let next, to;
                    //如果左右值都是无效值,则表示当前插入值被覆盖, 不进行插入
                    if (!DateBucketUtil.verify(leftTmp) && !DateBucketUtil.verify(rightTmp)) {
                        cursor.end();
                    } else {
                        //如果右值有效, 准备下一循环的数据(将当前的插入值替换为右值)
                        if (DateBucketUtil.verify(rightTmp)) {
                            next = rightTmp;
                            to = 1;
                        }
                        /*
                            如果左值有效, 判断右值是否已经有效, 若右值有效, 克隆当前循环状态并携带左值进入队列,
                            若右值无效, 准备下一循环的数据(将当前的插入值替换为左值)
                         */
                        if (DateBucketUtil.verify(leftTmp)) {
                            //如果右值也是有效值,则左值进入队列
                            if (next) {
                                let tmp = new Cursor(cursor, leftTmp);
                                cursors.push(tmp.toLeft());
                            } else {
                                next = leftTmp;
                                to = -1;
                            }
                        }
                        //替换当前插入值并调整二分法边界
                        item = next;
                        cursor.move(to);
                    }
                }
            }
            //如果当前已是数组中最后一个值,则进行插入操作
            if (cursor.isLast()) {
                cursor.insert(item);
            }
        }
        return this.arr;
    }

    /**
     * 二分法查询日期是否在日期段数组中, 如果在,返回时间段数据和索引值
     * @param date 日期
     * @returns {*} 返回对象包含索引值和时间段值
     */
    search(date){
        //处理时间段数据
        date = DateBucketUtil.dateHandler(date);
        let cursor = new Cursor(this.arr);
        //二分法循环体
        while (cursor.hasNext()) {
            //设置当前数组索引值
            cursor.setIndex();

            //获取数组当前位置的值
            let value = cursor.value();
            if (cursor.move(DateBucketUtil.dateInDateBucket(value, date))) {
                continue;
            }
            return {
                index: cursor.index,
                value
            };
        }
        return {
            index: -1,
            value: null
        };
    }

    /**
     * 判断时间值是否在时间段中, 偏左返回-1, 在其中返回0, 偏右返回1
     * @param dateBucket 时间段
     * @param date 时间值
     * @returns {number} 是否在时间段中, 偏左返回-1, 在其中返回0, 偏右返回1
     */
    static dateInDateBucket(dateBucket, date) {
        return (dateBucket.beginDate <= date && dateBucket.endDate >= date) ? 0 :
            (dateBucket.beginDate > date ? -1 : 1);
    }
    /**
     * 验证时间段是否有效
     * @param data 时间段
     * @returns {boolean} 是否有效
     */
    static verify(data) {
        return data.beginDate <= data.endDate;

    }

    /**
     * 是否两个时间段不相交
     * @param dataA 时间段A
     * @param dataB 时间段B
     * @returns {number} 是否两个不相交
     */
    static notCross(dataA, dataB) {
        return dataA.beginDate > dataB.endDate ? -1 :
            (dataB.beginDate > dataA.endDate ? 1 : 0);
    }

    /**
     * 时间段的时间值处理, 统一为时间戳数字(精确到秒)
     * @param date 时间段
     * @returns {*} 处理后的时间段
     */
    static dateBucketHandler(date) {
        date.beginDate = this.dateHandler(date.beginDate);
        date.endDate = this.dateHandler(date.endDate);
        return date;
    }
    /**
     * 时间值处理, 统一为时间戳数字(精确到秒)
     * @param date 时间值
     * @returns {*} 处理后的时间值
     */
    static dateHandler(date) {
        if (date instanceof Date) {
            date = date.setHours(0) / 1000;
        } else {
            date = new Date(date).setHours(0) / 1000;
        }
        return date;
    }
}

/**
 * 二分法游标工具
 */
class Cursor{
    /**
     * 被操作数组
     */
    arr;
    /**
     * 当前位置
     */
    index;
    /**
     * 左边界位置
     */
    left;
    /**
     * 右边界位置
     */
    right;
    /**
     * 方向,向左-1, 向右1
     */
    direction;
    /**
     * 携带数据
     */
    data;

    /**
     * 构造器,传入数组或者Cursor对象
     * @param arr 传入数组或者Cursor对象
     * @param data 携带数据
     */
    constructor(arr, data){
        if(!arr instanceof Array || !arr instanceof Cursor){
            throw Error("传入参数不正确, 应传入数组或者Cursor对象!");
        }

        if(arr instanceof Array) {
            this.index = -1;
            this.arr = arr;
            this.left = 0;
            this.right = arr.length;
            this.data = data;
        }else if(arr instanceof Cursor) {
            this.index = arr.index;
            this.arr = arr.arr;
            this.left = arr.left;
            this.right = arr.right;
            this.direction = arr.direction;
            if (data) {
                this.data = data;
            }else {
                this.data = arr.data;
            }
        }
    }

    /**
     * 返回当前位置的值
     * @returns {*} 返回当前位置的值
     */
    value(){
        return this.arr[this.index];
    }

    /**
     * 设置index值, 根据左边界和右边界
     * @returns {Cursor} 返回当前对象
     */
    setIndex(){
        this.index = (this.left + this.right) / 2 | 0;
        return this;
    }

    /**
     * 1 向右移动, -1 向左
     * @returns {Cursor} 返回当前对象
     */
    move(to){
        return to === 1 ? this.toRight() :
            (to === -1 ? this.toLeft() : null);
    }

    /**
     * 下一个值取左边
     * @returns {Cursor} 返回当前对象
     */
    toLeft(){
        this.right = this.index;
        this.direction = -1;
        return this;
    }

    /**
     * 下一个值取右边
     * @returns {Cursor} 返回当前对象
     */
    toRight(){
        this.left = this.index + 1;
        this.direction = 1;
        return this;
    }

    /**
     * 同数组的splice方法, 不过在操作同时会进行边界标记计算
     * @param deleteCount
     * @param items
     * @returns {Cursor} 返回当前对象
     */
    splice(deleteCount, ...items){
        this.right = this.right - deleteCount + items.length;
        this.arr.splice(this.index, deleteCount, ...items);
        return this;
    }

    /**
     * 插入值, 区分向左插入和向右插入
     * @param item 插入值
     * @returns {Cursor} 返回当前对象
     */
    insert(item){
        let index = this.index;
        if (this.direction === 1){
            index++;
        }
        this.arr.splice(index, 0, item);
        return this;
    }

    // noinspection JSUnusedGlobalSymbols
    /**
     * 是否向左
     * @returns {boolean}
     */
    isToLeft(){
        return this.direction < 0;
    }

    // noinspection JSUnusedGlobalSymbols
    /**
     * 是否向右
     * @returns {boolean}
     */
    isToRight(){
        return this.direction > 0;
    }

    /**
     * 是否是最后一个
     * @returns {*} 返回true或false
     */
    isLast(){
        return this.left === this.right;
    }

    /**
     * 是否还有下一个
     * @returns {*} 返回true或false
     */
    hasNext(){
        return this.left < this.right;
    }

    /**
     * 直接设置为结束位
     * @returns {*} 返回true或false
     */
    end(){
        this.left = this.right + 1;
        return this;
    }
}