console.log("this is script file")

//this is for nav bar make dynamic
const toggleSidebar = () => {
	if ($(".sidebar").is(":visible")) {
		$(".sidebar").css("display", "none")
		$(".content").css("margin-left", "0%")

	} else {

		$(".sidebar").css("display", "block")
		$(".content").css("margin-left", "20%")
	}
};
// end the nav bar dynamic

//it is for search bar
const search = () => {
	//console.log("serching...");
	let query = $("#search-input").val();
	if (query == '') {
		$(".search-result").hide();

	}
	else {
		console.log(query);

		//sending request to server
		let url = `http://localhost:8080/search/${query}`;

		fetch(url)
			.then((response) => {
				return response.json();
			})
			.then((data) => {
				//data....
				//console.log(data);
				let text = `<div class="list-group">`

				data.forEach((contact) => {
					text += `<a href="/user/contact/${contact.cid}" class='list-group-item list-group-item-action'>${contact.name}</a>`
				});

				text += `</div>`;

				$(".search-result").html(text);
				$(".search-result").show();
			});


	}
};
//it is for search bar end



//PAYMENT INTEGRATION
//first request to server to create order

const paymentStart = (() => {
	console.log("payemt started");
	let amount = $("#payment_field").val();
	console.log(amount);
	if (amount == "" || amount == null) {
			swal("Failed !!", "amount required", "error");
		return;
	}
	//code
	//we will use ajex to send request to server to create order - Jquery

	$.ajax({
		url: '/user/create_order',
		data: JSON.stringify({ amount: amount, info: 'order_request' }),
		contentType: 'application/json',
		type: 'POST',
		dataType: 'json',
		success: function(response) {
			//this function invoked when success
			//console.log(response);
			if (response.status == 'created') {
				//open payment form

				let options = {
					key: 'rzp_test_y2DFOeojzgACGh',
					amount: response.amount,
					currency: 'INR',
					name: 'Smart Contact Manager',
					description: 'Donation',
					image: 'https://user-images.githubusercontent.com/68143654/224338697-cf144185-528b-4756-9d01-d7d86264565f.png',
					order_id: response.id,

					handler: function(response) {
						console.log(response.razorpay_payement_id);
						console.log(response.razorpay_order_id);
						console.log(response.razorpay_signature);
						console.log("payment successfull");
						//alert("congrats")
						updatePaymentOnServer(response.razorpay_payement_id,response.razorpay_order_id,'paid');
						swal("Good job!", "payment successfull", "success");
					},
					"prefill": {
						"name": "",
						"email": "",
						"contact": ""
					},
					"notes": {
						"address": "Razorpay Corporate Office"
					},
					"theme": {
						"color": "#3399cc"
					},

				};

				//let rzp = new Razorpay(options);
				let rzp = new window.Razorpay(options);

				//var rzp = new Razorpay(options);
				rzp.on('payment.failed', function(response) {
					console.log(response.error.code);
					console.log(response.error.description);
					console.log(response.error.source);
					console.log(response.error.step);
					console.log(response.error.reason);
					console.log(response.error.metadata.order_id);
					console.log(response.error.metadata.payment_id);
					//alert("oops payment failed")
					swal("Failed", "oops payment failed", "error");
				});

				rzp.open();


			}
		},
		error: function(error) {
			//invoked when error occured
			console.log(error);
			alert("something went wrong");

		},
	}
	);





});



//updatePaymentOnServer

function updatePaymentOnServer(payment_id, order_id, status){ 
	$.ajax({
		url: '/user/update_order',
		data: JSON.stringify({ payment_id: payment_id, order_id:order_id, status:status }),
		contentType: 'application/json',
		type: 'POST',
		dataType: 'json',
		success:function(response){
			swal("Good job!", "payment successfull", "success");

		},
		error:function(error){
			swal("Failed", "your payment is successfully but we did not get on server , we will contact you as soon as possible", "error");
		}

	});

}








































