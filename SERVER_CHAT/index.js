var express = require("express");
var app = express();
var server = require("http").createServer(app);
var io = require("socket.io").listen(server);
var fs = require("fs");
server.listen(process.env.PORT || 3000);

var arrayUsername = [];

var Dangky = 0; // Thành công

io.sockets.on('connection', function (socket) { // socket chứa dữ liệu của thiets bị gửi lên server
	console.log("Co thiet bi ket noi!");

	// Nhận dữ liệu gửi từ Client lên: Đang ky Username
	socket.on('client-send-user', function (data) { // Biến gửi lên nằm trong biến data

		if (arrayUsername.indexOf(data) == -1){ // Không tồn tại trong mảng sẽ trả về giá trị -1
			arrayUsername.push(data);
			// Định danh Username
			socket.un = data; // Thay Mã bằng Username gửi lên thay cho mã:..Không làm thì server vấn hiểu nhé! Lấy theo máy đăng ký
			console.log("Register Success: " + data);			
			Dangky = 0; // Thanh coong
			// Tra danh sach User new dang ky thanh cong
			io.sockets.emit('server-send-listuser', { danhsach: arrayUsername }); // Gửi về arrayUser luôn nhé! Dạng JSONArray

		}else{
			console.log("Register Fail! Please Check Again!");
			Dangky = 1; // That bai
		}

		// emit tới máy nguoi vừa gửi
		socket.emit('server-sent-result', { ketqua: Dangky });

  });

	// Nhận dữ liệu gửi từ Client lên: Messager
	socket.on('client-sent-chat', function(mess){
		io.sockets.emit('server-send-mess', { mess: socket.un + ":" + mess }); // Gửi tới all
		console.log(socket.un + ":" + mess);
	})

});