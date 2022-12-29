<template>
    <!-- 按钮弹组件 -->
    
    <!-- <viewMark :visible.sync="visiblex" :url="this.urlx" ref="viewMark" @returnOAndNPic="returnOAndNPic" />
              <el-button @click="visiblex = true"></el-button> -->
    <!-- 相关属性 -->
    <!--
        
        urlx:'/static/img/404.68708c27.png',
        visiblex:false,
    -->
    <!-- 父组件方法 -->
    <!--  returnOAndNPic(newPicUrl,oldPicUrl){
      console.log("newPicUrl",newPicUrl)
      console.log("oldPicUrl",oldPicUrl)
      this.urlx=newPicUrl
    }, -->
    <el-dialog :visible.sync="openDialog" center>
        <div class="content">
            <canvas id="vasImpl" :width="width" :height="height" @mousedown="downEvent" @mouseup="upEvent"
                @mousemove="moveEvent" />
        </div>
        <span slot="footer">
            <el-button @click="savePic">保存</el-button>
            <el-button @click="restorePic">还原</el-button>
            <el-button @click="cancelMark">取消</el-button>
        </span>

    </el-dialog>

</template>

<script>
import axios from 'axios'
import request from '@/utils/request'
export default {
    props: {
        visible: {
            type: Boolean,
            default: false
        },
        url: {
            type: String
        },

        width: {
            type: Number,
            default: 500
        },
        height: {
            type: Number,
            default: 500
        },


    },
    computed: {
        openDialog: {
            get() {
                if (this.visible) {
                    this.initVas();
                }
                return this.visible;
            },
            set(val) {
                this.$emit('update:visible', val);
            }
        }
    },
    data() {
        return {
            comUploadUrl:"/api/file/Uploader/annexpic",
            actualUrl: '',
            ctx: '',
            element: '',
            inputImg: {
                imgData: ''
            },
            overImg: {
                imgData: ''
            },
            startPoint: {
                x: '',
                y: '',

            },
            targetImg: {
                imgData: ''
            },

            isDraw: false,


        }
    },

    methods: {
        // handleSuccess(res, file, fileList) {
        //     if (res.code == 200) {
        //         this.fileList.push({
        //             name: file.name,
        //             fileId: res.data.name,
        //             url: res.data.url,
        //         });
        //         this.$emit("input", this.fileList);
        //     } else {
        //         fileList.filter((o) => o.uid != file.uid);
        //         this.$emit("input", this.fileList);
        //         this.$message({ message: res.msg, type: "error", duration: 1500 });
        //     }
        // },
        initVas() {
            let that = this;
            this.$nextTick(() => {
                this.element = document.getElementById("vasImpl");
                this.ctx = this.element.getContext('2d');
                if (!this.ctx) {
                    console.log("ctx is null")
                    return;
                }
                // 设置颜色
                this.ctx.strokeStyle = 'red'
                this.ctx.lineWidth = 3
                let img = new Image(this.width, this.height);
                img.src = this.actualUrl;
                console.log("actualUrl", this.actualUrl)
                img.onerror = (e) => {
                    console.log(e)
                    alert('图片不能被加载。')
                }
                img.onload = () => {

                    that.ctx.drawImage(img, 0, 0, this.width, this.height);
                    that.inputImg.imgData = this.ctx.getImageData(0, 0, this.width, this.height)
                    this.overImg.imgData = this.ctx.getImageData(0, 0, this.width, this.height)
                }
            })
        },
        downEvent(e) {
            console.log("e", e)
            const canvasX = e.offsetX
            const canvasY = e.offsetY
            this.startPoint.x = canvasX
            this.startPoint.y = canvasY
            this.overImg.imgData = this.ctx.getImageData(0, 0, this.width, this.height)
            this.isDraw = true;
        },
        upEvent() {
            this.isDraw = false;

        },
        moveEvent(e) {

            if (this.isDraw) {

                let canvasX = e.offsetX
                let canvasY = e.offsetY

                this.ctx.putImageData(this.overImg.imgData, 0, 0)
                this.ctx.beginPath()
                let a = (canvasX - this.startPoint.x) / 2
                let b = (canvasY - this.startPoint.y) / 2
                this.drawEllipse(this.ctx, this.startPoint.x + a, this.startPoint.y + b, a > 0 ? a : -a, b > 0 ? b : -b)

                console.log("xxxx");
                this.ctx.stroke()
            }
        },
        drawEllipse(context, x, y, a, b) {
            console.log("aaa", x, y, a, b)
            context.save()
            var r = (a > b) ? a : b
            var ratioX = a / r
            var ratioY = b / r
            context.scale(ratioX, ratioY)
            context.beginPath()
            context.arc(x / ratioX, y / ratioY, r, 0, 2 * Math.PI, false)
            context.closePath()
            context.restore()
        },
        restorePic() {
            this.ctx.putImageData(this.inputImg.imgData, 0, 0)

        },
        savePic() {
            // this.element.toBlob(function (blob) {
            //     var data = new FormData();
            //     // 装载图片数据
            //     data.append('image', blob);
            //     // forms.append('file', blob)
            //     // 图片ajax上传，字段名是image
            //     var xhr = new XMLHttpRequest();
            //     // 文件上传成功
            //     xhr.onload = function () {
            //         // xhr.responseText就是返回的数据
            //     };
            //     // 开始上传
            //     xhr.open('POST', 'upload.php', true);
            //     xhr.send(data);
            // });
            // 登录状态下不会出现这行文字，点击页面右上角一键登录
            let that = this;
            this.element.toBlob(function (blob) {
                let forms = new FormData()
                // let configs = {
                //     headers: { 'Content-Type': 'multipart/form-data' }
                // };
                forms.append('file', blob)
                request({
                    url: that.comUploadUrl,
                    method: 'post',
                    data:forms,
                    headers: { 'Content-Type': 'multipart/form-data' }

                })
                // axios.post(that.comUploadUrl, forms, configs).then(res => {
                //     console.log("res",res)
                // },
                //     error => {
                //         console.log(error);
                //     })
            });
            let newPicUrl = '';
            let oldPicUrl = this.url;
            this.$emit("returnOAndNPic", newPicUrl, oldPicUrl);
            // this.ctx.putImageData(this.inputImg.imgData, 0, 0)
            var w = this.width;
            var h = this.height;
            this.ctx.clearRect(0, 0, w, h);
            this.openDialog = false;
        },
        cancelMark() {
            this.ctx.putImageData(this.inputImg.imgData, 0, 0)
            this.openDialog = false;
        }

    },
    mounted() {
        if (!this.url) {
            console.log("The value of this.url is :", this.url)
            return;
        }
        this.actualUrl = 'http://localhost:3000' + this.url;
        // this.actualUrl=this.define.APIURl+this.url;
    },
}
</script>

<style scoped>
.content {
    margin: 0;
    padding: 0;
    width: 500px;
    height: 500px;
}

/deep/.el-dialog__body {

    display: flex;
    justify-content: center;
}
</style>