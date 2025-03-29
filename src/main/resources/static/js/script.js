console.log("this is script file");

const toggleSidebar = () => {
    let sidebar = document.querySelector('.sidebar');
    let content = document.querySelector('.content');

    if (sidebar.style.display === "block") {
        sidebar.style.display = "none";
        content.style.marginLeft = "0%";
    } else {
        sidebar.style.display = "block";
        content.style.marginLeft = "20%";
    }
};

const search = () => {
	let query=$("#search-input").val();
	
	if(query==''){
		$(".search-result").hide();
	}else{
		//search
		
		//sending request to server
		let url=`http://localhost:8085/search/${query}`;
		fetch(url).then((response) => {
			return response.json();
		}).then((data) => {
			console.log(data);
			let text=`<div class="list-group">`
			
			data.forEach((contact) => {
				text+= `<a href="/user/contact/${contact.cId}" class='list-group-item list-group-item-action'>${contact.name}</a>`
			});
			
			text += `</div>`;
			$(".search-result").html(text);
			$(".search-result").show();
		});
		
		
		
	}
}