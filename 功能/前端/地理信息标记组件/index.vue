<template>
  <div>
    <el-dialog :visible.sync="visible" class="container-map">
      <div id="allmap"></div>
    </el-dialog>
  </div>
</template>
  
<script>
import mapIconPath from "@/assets/images/gree_point.png";
export default {
  name: "extend-map",
  data() {
    return {
      loading: true,
      visible: false,
      map: null,
      marker: null,
      //center position 为O对象（e.point）
      center: "",
      zoom: 15,
      iconOpt: { url: mapIconPath, size: { width: 32, height: 32 } },

      //回传数据
      position: "",
      address: "",
    };
  },
  props: {
    mapParams: {
      Object,
    },
  },
  watch: {
    visible(n, o) {
      if (!n) {
        this.$emit(
          "changeMapVisible",
          this.mapParams.isEdit,
          this.position.lng + "," + this.position.lat,
          this.address
        );
      }
    },
  },
  mounted() {
    //初始化：数据准备
    if (this.mapParams.lonLat && this.mapParams.address) {
      let lonLatArr = this.mapParams.lonLat.split(",");
      let point = new BMap.Point(Number(lonLatArr[0]), Number(lonLatArr[1]));
      this.center = point;
      this.position = point;
      this.address = this.mapParams.address;
    } else if (this.address) {
      this.address = this.address;
    } else if (this.mapParams.lonLat) {
      let lonLatArr = this.mapParams.lonLat.split(",");
      let point = new BMap.Point(Number(lonLatArr[0]), Number(lonLatArr[1]));
      this.center = point;
      this.position = point;
    } else {
      let lonLatArr = "116.404462,39.91593".split(",");
      let point = new BMap.Point(Number(lonLatArr[0]), Number(lonLatArr[1]));
      this.center = point;
      this.position = point;
      this.address = "北京市东城区北京市中华路甲10号";
    }
    this.visible = true;
  },
  methods: {
    init() {
      let target = document.getElementsByClassName("el-dialog__body")[0];
      this.loading = this.$loading({ target: target, fullscreen: false });
      let that = this;
      //初始化：数据处理
      if (this.address && this.center) {
        this.initMap();
        setTimeout(() => {
          that.$nextTick(() => {
            that.$message({
              message:
                "经纬度：" +
                that.position.lng +
                "," +
                that.position.lat +
                "地址：" +
                that.address,
              type: "success",
            });
          });
        }, 500);
      } else if (this.address) {
        console.log("地址转坐标");
        //地址转坐标
        this.addressToPoint();

        setTimeout(() => {
          that.$nextTick(() => {
            that.$message({
              message:
                "经纬度：" +
                that.position.lng +
                "," +
                that.position.lat +
                "地址：" +
                that.address,
              type: "success",
            });
          });
        }, 500);
      } else if (this.center) {
        //坐标转地址
        console.log("坐标转地址");
        this.PointToAddress(this.center, "success");
        this.initMap();
      } else {
        console.log("传入地址坐标都为空2");
      }

      setTimeout(() => {
        that.$nextTick(() => {
          this.loading.close();
        });
      }, 500);
    },
    //初始化地图
    initMap() {
      let that = this;
      console.log("position", this.center);
      this.map = new BMap.Map("allmap");
      this.map.centerAndZoom(this.center, this.zoom);
      this.map.enableScrollWheelZoom(true);
      let myIcon = new BMap.Icon(this.iconOpt.url, this.iconOpt.size); // 设置图片偏移    );
      let point = new BMap.Point(this.position.lng, this.position.lat);
      this.marker = new BMap.Marker(point); // 创建标注
      this.marker.setIcon(myIcon);
      this.map.addOverlay(this.marker);
      this.map.addEventListener("click", (e) => {
        that.marker.setPosition(e.point);
        that.position = e.point;
        that.PointToAddress(e.point, "info");
      });
    },

    addressToPoint() {
      let that = this;
      const geoCoder = new BMap.Geocoder();
      new Promise((resolve, reject) => {
        console.log("that", that.address);
        geoCoder.getPoint(that.address, function (point) {
          if (!point) {
            console.log("地址转点解析失败返回");
            return;
          }

          that.center = point;
          that.position = point;

          console.log("point", point);
          resolve();
        });
      }).then(() => {
        that.initMap();
      });
    },
    PointToAddress(point, type) {
      let that = this;
      const geoCoder = new BMap.Geocoder();

      new Promise((resolve, reject) => {
        geoCoder.getLocation(point, function (res) {
          console.log("获取经纬度", point, "获取详细地址", res);
          if (!res) {
            console.log("点转地址解析失败返回");
            return;
          }
          if (res.addressComponents.street) {
            const addressObject = res.addressComponents;
            that.address =
              addressObject.city +
              addressObject.district +
              addressObject.province +
              addressObject.street +
              addressObject.streetNumber;
            console.log("返回地址为", that.address);
          } else {
            that.address = "";
            console.log("返回地址为", that.address);
          }
          resolve();
        });
      }).then(() => {
        that.$message({
          message:
            "经纬度：" +
            that.position.lng +
            "," +
            that.position.lat +
            "地址：" +
            that.address,
          type,
        });
      });
    },
  },
};
</script>
<style lang="scss" scoped>
>>> .el-dialog__body {
  text-align: center;
  overflow: hidden;
}

#allmap {
  width: 100%;
  height: 400px;
}
</style>
  