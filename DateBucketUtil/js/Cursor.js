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

    // /**
    //  * 获取右边界, 若超过数组边界则重置
    //  * @returns {*} 返回右边界位置
    //  */
    // get right(){
    //     let len = this.arr.lenght;
    //     if(this.right >= len){
    //         this.right = len - 1;
    //     }
    //     return this.right;
    // }
}